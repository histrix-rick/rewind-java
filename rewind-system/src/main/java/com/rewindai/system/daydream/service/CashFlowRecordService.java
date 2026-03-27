package com.rewindai.system.daydream.service;

import com.rewindai.common.core.exception.BusinessException;
import com.rewindai.common.core.result.ErrorCode;
import com.rewindai.system.daydream.entity.CashFlowRecord;
import com.rewindai.system.daydream.entity.Daydream;
import com.rewindai.system.daydream.entity.DreamContext;
import com.rewindai.system.daydream.enums.CashTransactionType;
import com.rewindai.system.daydream.repository.CashFlowRecordRepository;
import com.rewindai.system.daydream.repository.DreamContextRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * 现金流转记录 Service
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CashFlowRecordService {

    private final CashFlowRecordRepository cashFlowRecordRepository;
    private final DreamContextRepository dreamContextRepository;
    private final DaydreamService daydreamService;

    public List<CashFlowRecord> getRecordsByDream(UUID dreamId) {
        return cashFlowRecordRepository.findByDreamIdOrderByCreatedAtAsc(dreamId);
    }

    public List<CashFlowRecord> getRecordsByDreamAndNode(UUID dreamId, UUID nodeId) {
        return cashFlowRecordRepository.findByDreamIdAndNodeIdOrderByCreatedAtAsc(dreamId, nodeId);
    }

    /**
     * 添加现金流转记录并更新余额
     */
    @Transactional
    public CashFlowRecord addRecord(UUID userId, UUID dreamId, UUID nodeId,
                                      CashTransactionType transactionType,
                                      BigDecimal amount, String description,
                                      UUID relatedAssetId) {
        Daydream daydream = daydreamService.findByIdAndUserId(dreamId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DREAM_NOT_FOUND));

        // 获取当前现金余额
        DreamContext context = dreamContextRepository.findFirstByDreamIdOrderByCreatedAtDesc(dreamId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "梦境上下文不存在"));

        BigDecimal currentBalance = context.getFinancialAmount();
        if (currentBalance == null) {
            currentBalance = BigDecimal.ZERO;
        }

        // 计算变动后的余额
        BigDecimal changeAmount = calculateChangeAmount(transactionType, amount);
        BigDecimal newBalance = currentBalance.add(changeAmount);

        // 创建记录
        CashFlowRecord record = CashFlowRecord.builder()
                .dreamId(dreamId)
                .nodeId(nodeId)
                .transactionType(transactionType)
                .amount(amount)
                .balanceBefore(currentBalance)
                .balanceAfter(newBalance)
                .description(description)
                .relatedAssetId(relatedAssetId)
                .build();

        CashFlowRecord saved = cashFlowRecordRepository.save(record);

        // 更新DreamContext中的余额
        context.setFinancialAmount(newBalance);
        dreamContextRepository.save(context);

        log.info("现金流转记录添加成功: dreamId={}, nodeId={}, type={}, amount={}, balance={} -> {}",
                dreamId, nodeId, transactionType, amount, currentBalance, newBalance);

        return saved;
    }

    /**
     * 计算实际变动金额（考虑交易类型）
     */
    private BigDecimal calculateChangeAmount(CashTransactionType type, BigDecimal amount) {
        return switch (type) {
            case EARN, DIVEST -> amount; // 收入或撤资：增加现金
            case SPEND, INVEST -> amount.negate(); // 支出或投资：减少现金
        };
    }

    /**
     * 仅添加记录，不更新余额（用于回滚等场景）
     */
    @Transactional
    public CashFlowRecord addRecordWithoutBalanceUpdate(UUID userId, UUID dreamId, UUID nodeId,
                                                          CashTransactionType transactionType,
                                                          BigDecimal amount, BigDecimal balanceBefore,
                                                          BigDecimal balanceAfter, String description,
                                                          UUID relatedAssetId) {
        Daydream daydream = daydreamService.findByIdAndUserId(dreamId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DREAM_NOT_FOUND));

        CashFlowRecord record = CashFlowRecord.builder()
                .dreamId(dreamId)
                .nodeId(nodeId)
                .transactionType(transactionType)
                .amount(amount)
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .description(description)
                .relatedAssetId(relatedAssetId)
                .build();

        return cashFlowRecordRepository.save(record);
    }

    @Transactional
    public void deleteByDreamIdAndNodeId(UUID dreamId, UUID nodeId) {
        cashFlowRecordRepository.deleteByDreamIdAndNodeId(dreamId, nodeId);
    }
}
