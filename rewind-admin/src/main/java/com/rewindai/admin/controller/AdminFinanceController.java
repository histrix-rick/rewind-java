package com.rewindai.admin.controller;

import com.rewindai.admin.dto.FinancialReportResponse;
import com.rewindai.admin.dto.RewardConfigRequest;
import com.rewindai.admin.dto.RewardConfigResponse;
import com.rewindai.common.core.result.Result;
import com.rewindai.system.wallet.entity.RewardConfig;
import com.rewindai.system.wallet.entity.WalletTransaction;
import com.rewindai.system.wallet.enums.TransactionType;
import com.rewindai.system.wallet.repository.RewardConfigRepository;
import com.rewindai.system.wallet.repository.WalletTransactionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 后台管理 - 财务管理控制器
 *
 * @author Rewind.ai Team
 */
@Slf4j
@RestController
@RequestMapping("/admin/finance")
@RequiredArgsConstructor
@Tag(name = "后台管理-财务管理", description = "后台管理系统财务管理接口")
public class AdminFinanceController {

    private final RewardConfigRepository rewardConfigRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // ========== 奖励配置管理 ==========

    @GetMapping("/reward-configs")
    @Operation(summary = "获取奖励配置列表", description = "分页获取奖励配置列表")
    public Result<Page<RewardConfigResponse>> getRewardConfigs(
            @RequestParam(required = false) String rewardType,
            @RequestParam(required = false) Boolean isActive,
            @PageableDefault(size = 20, sort = "sortOrder") Pageable pageable) {

        Page<RewardConfig> configs;
        if (rewardType != null && !rewardType.isEmpty()) {
            configs = rewardConfigRepository.findByRewardTypeContaining(rewardType, pageable);
        } else if (isActive != null) {
            configs = rewardConfigRepository.findByIsActive(isActive, pageable);
        } else {
            configs = rewardConfigRepository.findAll(pageable);
        }

        Page<RewardConfigResponse> responsePage = configs.map(RewardConfigResponse::fromEntity);
        return Result.success(responsePage);
    }

    @GetMapping("/reward-configs/{id}")
    @Operation(summary = "获取奖励配置详情", description = "根据ID获取奖励配置详情")
    public Result<RewardConfigResponse> getRewardConfigById(@PathVariable Long id) {
        return rewardConfigRepository.findById(id)
                .map(RewardConfigResponse::fromEntity)
                .map(Result::success)
                .orElse(Result.notFound("奖励配置不存在"));
    }

    @PostMapping("/reward-configs")
    @Operation(summary = "创建奖励配置", description = "创建新的奖励配置")
    public Result<RewardConfigResponse> createRewardConfig(@Valid @RequestBody RewardConfigRequest request) {
        RewardConfig config = RewardConfig.builder()
                .rewardType(request.getRewardType())
                .rewardName(request.getRewardName())
                .description(request.getDescription())
                .rewardAmount(request.getRewardAmount())
                .dailyLimit(request.getDailyLimit())
                .totalLimit(request.getTotalLimit())
                .minLevel(request.getMinLevel())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .build();

        RewardConfig saved = rewardConfigRepository.save(config);
        log.info("创建奖励配置: id={}, type={}", saved.getId(), saved.getRewardType());
        return Result.success(RewardConfigResponse.fromEntity(saved));
    }

    @PutMapping("/reward-configs/{id}")
    @Operation(summary = "更新奖励配置", description = "更新奖励配置")
    public Result<RewardConfigResponse> updateRewardConfig(
            @PathVariable Long id,
            @Valid @RequestBody RewardConfigRequest request) {

        return rewardConfigRepository.findById(id)
                .map(config -> {
                    config.setRewardType(request.getRewardType());
                    config.setRewardName(request.getRewardName());
                    config.setDescription(request.getDescription());
                    config.setRewardAmount(request.getRewardAmount());
                    config.setDailyLimit(request.getDailyLimit());
                    config.setTotalLimit(request.getTotalLimit());
                    config.setMinLevel(request.getMinLevel());
                    if (request.getIsActive() != null) {
                        config.setIsActive(request.getIsActive());
                    }
                    if (request.getSortOrder() != null) {
                        config.setSortOrder(request.getSortOrder());
                    }
                    RewardConfig saved = rewardConfigRepository.save(config);
                    log.info("更新奖励配置: id={}", saved.getId());
                    return Result.success(RewardConfigResponse.fromEntity(saved));
                })
                .orElse(Result.notFound("奖励配置不存在"));
    }

    @DeleteMapping("/reward-configs/{id}")
    @Operation(summary = "删除奖励配置", description = "删除奖励配置")
    public Result<Void> deleteRewardConfig(@PathVariable Long id) {
        rewardConfigRepository.deleteById(id);
        log.info("删除奖励配置: id={}", id);
        return Result.success();
    }

    @PostMapping("/reward-configs/{id}/toggle")
    @Operation(summary = "切换奖励配置状态", description = "启用/禁用奖励配置")
    public Result<RewardConfigResponse> toggleRewardConfig(@PathVariable Long id) {
        return rewardConfigRepository.findById(id)
                .map(config -> {
                    config.setIsActive(!config.getIsActive());
                    RewardConfig saved = rewardConfigRepository.save(config);
                    log.info("切换奖励配置状态: id={}, isActive={}", saved.getId(), saved.getIsActive());
                    return Result.success(RewardConfigResponse.fromEntity(saved));
                })
                .orElse(Result.notFound("奖励配置不存在"));
    }

    // ========== 财务报表 ==========

    @GetMapping("/report")
    @Operation(summary = "获取财务报表", description = "获取财务统计数据和趋势图表")
    public Result<FinancialReportResponse> getFinancialReport() {
        LocalDate today = LocalDate.now();
        OffsetDateTime todayStart = today.atStartOfDay().atOffset(ZoneOffset.UTC);

        // 获取所有交易
        List<WalletTransaction> allTransactions = walletTransactionRepository.findAll(
                Sort.by(Sort.Direction.ASC, "createdAt")
        );

        // 计算总体统计
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;
        BigDecimal todayIncome = BigDecimal.ZERO;
        BigDecimal todayExpense = BigDecimal.ZERO;
        long todayCount = 0;

        Map<String, BigDecimal> dailyIncomeMap = new LinkedHashMap<>();
        Map<String, BigDecimal> dailyExpenseMap = new LinkedHashMap<>();
        Map<TransactionType, TypeStatsBuilder> typeStatsMap = new HashMap<>();

        // 初始化最近7天
        for (int i = 6; i >= 0; i--) {
            String date = today.minusDays(i).format(DATE_FORMATTER);
            dailyIncomeMap.put(date, BigDecimal.ZERO);
            dailyExpenseMap.put(date, BigDecimal.ZERO);
        }

        for (WalletTransaction tx : allTransactions) {
            BigDecimal amount = tx.getAmount();
            String date = tx.getCreatedAt().toLocalDate().format(DATE_FORMATTER);
            boolean isIncome = isIncomeType(tx.getTransactionType());

            // 总体统计
            if (isIncome) {
                totalIncome = totalIncome.add(amount);
            } else {
                totalExpense = totalExpense.add(amount);
            }

            // 今日统计
            if (tx.getCreatedAt().isAfter(todayStart)) {
                if (isIncome) {
                    todayIncome = todayIncome.add(amount);
                } else {
                    todayExpense = todayExpense.add(amount);
                }
                todayCount++;
            }

            // 每日趋势
            if (dailyIncomeMap.containsKey(date)) {
                if (isIncome) {
                    dailyIncomeMap.put(date, dailyIncomeMap.get(date).add(amount));
                } else {
                    dailyExpenseMap.put(date, dailyExpenseMap.get(date).add(amount));
                }
            }

            // 类型统计
            TypeStatsBuilder builder = typeStatsMap.computeIfAbsent(
                    tx.getTransactionType(),
                    k -> new TypeStatsBuilder(tx.getTransactionType())
            );
            builder.add(amount);
        }

        // 构建总体统计
        FinancialReportResponse.OverallStats overallStats = FinancialReportResponse.OverallStats.builder()
                .totalTransactionAmount(totalIncome.add(totalExpense))
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .totalTransactionCount((long) allTransactions.size())
                .todayIncome(todayIncome)
                .todayExpense(todayExpense)
                .todayTransactionCount(todayCount)
                .build();

        // 构建趋势数据
        FinancialReportResponse.TrendData incomeTrend = FinancialReportResponse.TrendData.builder()
                .dates(new ArrayList<>(dailyIncomeMap.keySet()))
                .amounts(new ArrayList<>(dailyIncomeMap.values()))
                .build();

        FinancialReportResponse.TrendData expenseTrend = FinancialReportResponse.TrendData.builder()
                .dates(new ArrayList<>(dailyExpenseMap.keySet()))
                .amounts(new ArrayList<>(dailyExpenseMap.values()))
                .build();

        // 构建类型统计
        List<FinancialReportResponse.TypeStats> typeStatsList = new ArrayList<>();
        BigDecimal totalAmount = totalIncome.add(totalExpense);
        for (TypeStatsBuilder builder : typeStatsMap.values()) {
            BigDecimal percentage = totalAmount.compareTo(BigDecimal.ZERO) > 0
                    ? builder.getAmount().multiply(BigDecimal.valueOf(100))
                            .divide(totalAmount, 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            typeStatsList.add(FinancialReportResponse.TypeStats.builder()
                    .transactionType(builder.getType().name())
                    .typeName(builder.getType().getDesc())
                    .amount(builder.getAmount())
                    .count(builder.getCount())
                    .percentage(percentage)
                    .build());
        }

        // 按金额排序
        typeStatsList.sort((a, b) -> b.getAmount().compareTo(a.getAmount()));

        FinancialReportResponse report = FinancialReportResponse.builder()
                .overallStats(overallStats)
                .incomeTrend(incomeTrend)
                .expenseTrend(expenseTrend)
                .typeStats(typeStatsList)
                .build();

        return Result.success(report);
    }

    private boolean isIncomeType(TransactionType type) {
        return type == TransactionType.REWARD ||
               type == TransactionType.SHARE ||
               type == TransactionType.TRANSFER_IN ||
               type == TransactionType.ADMIN_GRANT ||
               type == TransactionType.REWARD_RECEIVE;
    }

    private static class TypeStatsBuilder {
        private final TransactionType type;
        private BigDecimal amount = BigDecimal.ZERO;
        private long count = 0;

        TypeStatsBuilder(TransactionType type) {
            this.type = type;
        }

        void add(BigDecimal amount) {
            this.amount = this.amount.add(amount);
            this.count++;
        }

        TransactionType getType() { return type; }
        BigDecimal getAmount() { return amount; }
        long getCount() { return count; }
    }
}
