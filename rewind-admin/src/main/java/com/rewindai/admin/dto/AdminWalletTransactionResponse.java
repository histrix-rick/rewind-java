package com.rewindai.admin.dto;

import com.rewindai.system.wallet.entity.WalletTransaction;
import com.rewindai.system.wallet.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 后台管理 - 钱包交易记录响应 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "后台钱包交易记录响应")
public class AdminWalletTransactionResponse {

    @Schema(description = "交易记录ID")
    private UUID id;

    @Schema(description = "用户ID")
    private UUID userId;

    @Schema(description = "用户昵称")
    private String userNickname;

    @Schema(description = "用户头像")
    private String userAvatar;

    @Schema(description = "交易单号")
    private String transactionNo;

    @Schema(description = "交易类型")
    private TransactionType transactionType;

    @Schema(description = "交易金额")
    private BigDecimal amount;

    @Schema(description = "交易前余额")
    private BigDecimal balanceBefore;

    @Schema(description = "交易后余额")
    private BigDecimal balanceAfter;

    @Schema(description = "交易描述")
    private String description;

    @Schema(description = "关联ID")
    private UUID referenceId;

    @Schema(description = "关联类型")
    private String relatedType;

    @Schema(description = "交易时间")
    private OffsetDateTime createdAt;

    /**
     * 从实体转换
     */
    public static AdminWalletTransactionResponse fromEntity(WalletTransaction tx) {
        return AdminWalletTransactionResponse.builder()
                .id(tx.getId())
                .userId(tx.getUserId())
                .transactionNo(tx.getTransactionNo())
                .transactionType(tx.getTransactionType())
                .amount(tx.getAmount())
                .balanceBefore(tx.getBalanceBefore())
                .balanceAfter(tx.getBalanceAfter())
                .description(tx.getDescription())
                .referenceId(tx.getRelatedId())
                .relatedType(tx.getRelatedType())
                .createdAt(tx.getCreatedAt())
                .build();
    }
}
