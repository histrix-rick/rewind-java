package com.rewindai.app.controller;

import com.rewindai.app.dto.TransactionResponse;
import com.rewindai.app.dto.WalletResponse;
import com.rewindai.common.core.result.Result;
import com.rewindai.system.wallet.entity.UserWallet;
import com.rewindai.system.wallet.entity.WalletTransaction;
import com.rewindai.system.wallet.enums.TransactionType;
import com.rewindai.system.wallet.service.UserWalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 钱包 Controller
 *
 * @author Rewind.ai Team
 */
@Tag(name = "钱包管理", description = "梦想币钱包相关接口")
@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final UserWalletService userWalletService;

    @Operation(summary = "获取我的钱包")
    @GetMapping
    public Result<WalletResponse> getWallet(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        UserWallet wallet = userWalletService.getOrCreateWallet(userId);
        return Result.success(WalletResponse.from(wallet));
    }

    @Operation(summary = "获取交易记录")
    @GetMapping("/transactions")
    public Result<Page<TransactionResponse>> getTransactions(
            @RequestParam(required = false) TransactionType type,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());

        Page<WalletTransaction> transactions;
        if (type != null) {
            transactions = userWalletService.getTransactionsByType(userId, type, pageable);
        } else {
            transactions = userWalletService.getTransactions(userId, pageable);
        }

        return Result.success(transactions.map(TransactionResponse::from));
    }
}
