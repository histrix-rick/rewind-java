package com.rewindai.admin.controller;

import com.rewindai.admin.dto.AdminWalletResponse;
import com.rewindai.admin.dto.AdminWalletTransactionResponse;
import com.rewindai.common.core.result.Result;
import com.rewindai.system.user.entity.User;
import com.rewindai.system.user.repository.UserRepository;
import com.rewindai.system.wallet.entity.UserWallet;
import com.rewindai.system.wallet.entity.WalletTransaction;
import com.rewindai.system.wallet.enums.TransactionType;
import com.rewindai.system.wallet.repository.UserWalletRepository;
import com.rewindai.system.wallet.repository.WalletTransactionRepository;
import com.rewindai.system.wallet.service.UserWalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 后台管理 - 钱包管理控制器
 *
 * @author Rewind.ai Team
 */
@Slf4j
@RestController
@RequestMapping("/admin/wallet")
@RequiredArgsConstructor
@Tag(name = "后台管理-钱包管理", description = "后台管理系统钱包管理接口")
public class AdminWalletController {

    private final UserWalletService userWalletService;
    private final UserWalletRepository userWalletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final UserRepository userRepository;

    @GetMapping("/list")
    @Operation(summary = "获取钱包列表", description = "分页获取所有用户钱包列表")
    public Result<Page<AdminWalletResponse>> getWalletList(
            @RequestParam(required = false) UUID userId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<UserWallet> wallets;
        if (userId != null) {
            wallets = userWalletRepository.findByUserId(userId, pageable);
        } else {
            wallets = userWalletRepository.findAll(pageable);
        }

        // 批量获取用户信息
        var userIds = wallets.getContent().stream()
                .map(UserWallet::getUserId)
                .collect(Collectors.toSet());
        var users = userRepository.findAllByIdIn(userIds);
        var userMap = users.stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        Page<AdminWalletResponse> responsePage = wallets.map(wallet -> {
            var response = AdminWalletResponse.fromEntity(wallet);
            var user = userMap.get(wallet.getUserId());
            if (user != null) {
                response.setUserNickname(user.getNickname());
                response.setUserAvatar(user.getAvatarUrl());
            }
            return response;
        });

        return Result.success(responsePage);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户钱包", description = "获取指定用户的钱包信息")
    public Result<AdminWalletResponse> getUserWallet(@PathVariable UUID userId) {
        var walletOpt = userWalletRepository.findByUserId(userId);
        if (walletOpt.isEmpty()) {
            return Result.notFound("用户钱包不存在");
        }
        var wallet = walletOpt.get();
        var response = AdminWalletResponse.fromEntity(wallet);
        userRepository.findById(userId).ifPresent(user -> {
            response.setUserNickname(user.getNickname());
            response.setUserAvatar(user.getAvatarUrl());
        });
        return Result.success(response);
    }

    @GetMapping("/user/{userId}/transactions")
    @Operation(summary = "获取用户交易记录", description = "分页获取指定用户的交易记录")
    public Result<Page<AdminWalletTransactionResponse>> getUserTransactions(
            @PathVariable UUID userId,
            @RequestParam(required = false) TransactionType type,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<WalletTransaction> transactions;
        if (type != null) {
            transactions = walletTransactionRepository.findByUserIdAndTransactionType(userId, type, pageable);
        } else {
            transactions = walletTransactionRepository.findByUserId(userId, pageable);
        }

        Page<AdminWalletTransactionResponse> responsePage = transactions.map(AdminWalletTransactionResponse::fromEntity);
        return Result.success(responsePage);
    }

    @PostMapping("/user/{userId}/grant")
    @Operation(summary = "管理员发放梦想币", description = "向指定用户发放梦想币")
    public Result<WalletTransaction> grantCoins(
            @PathVariable UUID userId,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String description,
            Authentication authentication) {
        UUID adminId = UUID.fromString(authentication.getName());
        String desc = description != null ? description : "管理员发放";
        WalletTransaction transaction = userWalletService.adminGrant(userId, amount, desc, adminId);
        log.info("管理员发放梦想币: adminId={}, userId={}, amount={}", adminId, userId, amount);
        return Result.success(transaction);
    }

    @PostMapping("/user/{userId}/deduct")
    @Operation(summary = "管理员扣除梦想币", description = "从指定用户扣除梦想币")
    public Result<WalletTransaction> deductCoins(
            @PathVariable UUID userId,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String description,
            Authentication authentication) {
        UUID adminId = UUID.fromString(authentication.getName());
        String desc = description != null ? description : "管理员扣除";
        WalletTransaction transaction = userWalletService.adminDeduct(userId, amount, desc, adminId);
        log.info("管理员扣除梦想币: adminId={}, userId={}, amount={}", adminId, userId, amount);
        return Result.success(transaction);
    }

    @GetMapping("/transactions")
    @Operation(summary = "获取所有交易记录", description = "分页获取所有交易记录")
    public Result<Page<AdminWalletTransactionResponse>> getAllTransactions(
            @RequestParam(required = false) TransactionType type,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<WalletTransaction> transactions;
        if (type != null) {
            transactions = walletTransactionRepository.findByTransactionType(type, pageable);
        } else {
            transactions = walletTransactionRepository.findAll(pageable);
        }

        Page<AdminWalletTransactionResponse> responsePage = transactions.map(AdminWalletTransactionResponse::fromEntity);
        return Result.success(responsePage);
    }

    @GetMapping("/stats")
    @Operation(summary = "获取钱包统计", description = "获取钱包总余额、总收入、总支出等统计")
    public Result<Map<String, Object>> getWalletStats() {
        long totalCount = userWalletRepository.count();
        var totalBalance = userWalletRepository.sumAllBalance();
        var totalEarned = userWalletRepository.sumAllEarned();
        var totalSpent = userWalletRepository.sumAllSpent();

        return Result.success(Map.of(
                "totalCount", totalCount,
                "totalBalance", totalBalance,
                "totalEarned", totalEarned,
                "totalSpent", totalSpent
        ));
    }
}
