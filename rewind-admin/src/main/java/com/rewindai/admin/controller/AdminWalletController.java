package com.rewindai.admin.controller;

import com.rewindai.common.core.result.Result;
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
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

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

    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户钱包", description = "获取指定用户的钱包信息")
    public Result<UserWallet> getUserWallet(@PathVariable UUID userId) {
        return userWalletRepository.findByUserId(userId)
                .map(Result::success)
                .orElse(Result.notFound("用户钱包不存在"));
    }

    @GetMapping("/user/{userId}/transactions")
    @Operation(summary = "获取用户交易记录", description = "分页获取指定用户的交易记录")
    public Result<Page<WalletTransaction>> getUserTransactions(
            @PathVariable UUID userId,
            @RequestParam(required = false) TransactionType type,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        Page<WalletTransaction> transactions;
        if (type != null) {
            transactions = walletTransactionRepository.findByUserIdAndTransactionType(userId, type, pageable);
        } else {
            transactions = walletTransactionRepository.findByUserId(userId, pageable);
        }
        return Result.success(transactions);
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
        return Result.success(transaction);
    }

    @GetMapping("/transactions")
    @Operation(summary = "获取所有交易记录", description = "分页获取所有交易记录")
    public Result<Page<WalletTransaction>> getAllTransactions(
            @RequestParam(required = false) TransactionType type,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        Page<WalletTransaction> transactions;
        if (type != null) {
            transactions = walletTransactionRepository.findByTransactionType(type, pageable);
        } else {
            transactions = walletTransactionRepository.findAll(pageable);
        }
        return Result.success(transactions);
    }
}
