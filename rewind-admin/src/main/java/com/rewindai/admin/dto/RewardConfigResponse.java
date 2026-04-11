package com.rewindai.admin.dto;

import com.rewindai.system.wallet.entity.RewardConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * 后台管理 - 奖励配置响应DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "奖励配置响应")
public class RewardConfigResponse {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "奖励类型")
    private String rewardType;

    @Schema(description = "奖励名称")
    private String rewardName;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "奖励金额")
    private BigDecimal rewardAmount;

    @Schema(description = "每日限制")
    private Integer dailyLimit;

    @Schema(description = "总限制")
    private Integer totalLimit;

    @Schema(description = "最低等级")
    private Integer minLevel;

    @Schema(description = "是否启用")
    private Boolean isActive;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "创建时间")
    private OffsetDateTime createdAt;

    @Schema(description = "更新时间")
    private OffsetDateTime updatedAt;

    public static RewardConfigResponse fromEntity(RewardConfig config) {
        return RewardConfigResponse.builder()
                .id(config.getId())
                .rewardType(config.getRewardType())
                .rewardName(config.getRewardName())
                .description(config.getDescription())
                .rewardAmount(config.getRewardAmount())
                .dailyLimit(config.getDailyLimit())
                .totalLimit(config.getTotalLimit())
                .minLevel(config.getMinLevel())
                .isActive(config.getIsActive())
                .sortOrder(config.getSortOrder())
                .createdAt(config.getCreatedAt())
                .updatedAt(config.getUpdatedAt())
                .build();
    }
}
