package com.rewindai.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 后台管理 - 奖励配置请求DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "奖励配置请求")
public class RewardConfigRequest {

    @NotBlank(message = "奖励类型不能为空")
    @Schema(description = "奖励类型")
    private String rewardType;

    @NotBlank(message = "奖励名称不能为空")
    @Schema(description = "奖励名称")
    private String rewardName;

    @Schema(description = "描述")
    private String description;

    @NotNull(message = "奖励金额不能为空")
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
}
