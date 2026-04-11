package com.rewindai.admin.dto;

import com.rewindai.system.wallet.entity.UserWallet;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 后台管理 - 用户钱包响应 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "后台用户钱包响应")
public class AdminWalletResponse {

    @Schema(description = "钱包ID")
    private UUID id;

    @Schema(description = "用户ID")
    private UUID userId;

    @Schema(description = "用户昵称")
    private String userNickname;

    @Schema(description = "用户头像")
    private String userAvatar;

    @Schema(description = "当前余额")
    private BigDecimal balance;

    @Schema(description = "累计收入")
    private BigDecimal totalEarned;

    @Schema(description = "累计支出")
    private BigDecimal totalSpent;

    @Schema(description = "冻结金额")
    private BigDecimal frozenAmount;

    @Schema(description = "创建时间")
    private OffsetDateTime createdAt;

    @Schema(description = "更新时间")
    private OffsetDateTime updatedAt;

    /**
     * 从实体转换
     */
    public static AdminWalletResponse fromEntity(UserWallet wallet) {
        return AdminWalletResponse.builder()
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
