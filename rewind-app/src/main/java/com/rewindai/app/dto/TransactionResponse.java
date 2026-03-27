package com.rewindai.app.dto;

import com.rewindai.system.wallet.entity.WalletTransaction;
import com.rewindai.system.wallet.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 交易记录响应 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {

    private UUID id;
    private String transactionNo;
    private TransactionType transactionType;
    private BigDecimal amount;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private String description;
    private UUID relatedId;
    private String relatedType;
    private OffsetDateTime createdAt;

    public static TransactionResponse from(WalletTransaction tx) {
        return TransactionResponse.builder()
                .id(tx.getId())
                .transactionNo(tx.getTransactionNo())
                .transactionType(tx.getTransactionType())
                .amount(tx.getAmount())
                .balanceBefore(tx.getBalanceBefore())
                .balanceAfter(tx.getBalanceAfter())
                .description(tx.getDescription())
                .relatedId(tx.getRelatedId())
                .relatedType(tx.getRelatedType())
                .createdAt(tx.getCreatedAt())
                .build();
    }
}
