package com.rewindai.app.dto;

import com.rewindai.system.wallet.entity.UserWallet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 钱包响应 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletResponse {

    private UUID id;
    private UUID userId;
    private BigDecimal balance;
    private BigDecimal totalEarned;
    private BigDecimal totalSpent;
    private BigDecimal frozenAmount;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public static WalletResponse from(UserWallet wallet) {
        return WalletResponse.builder()
                .id(wallet.getId())
                .userId(wallet.getUserId())
                .balance(wallet.getBalance())
                .totalEarned(wallet.getTotalEarned())
                .totalSpent(wallet.getTotalSpent())
                .frozenAmount(wallet.getFrozenAmount())
                .createdAt(wallet.getCreatedAt())
                .updatedAt(wallet.getUpdatedAt())
                .build();
    }
}
