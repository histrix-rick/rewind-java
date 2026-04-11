package com.rewindai.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 打赏请求 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Schema(description = "打赏请求")
public class RewardRequest {

    @NotNull(message = "打赏金额不能为空")
    @DecimalMin(value = "1", message = "打赏金额最少为1梦想币")
    @Schema(description = "打赏金额", required = true)
    private BigDecimal amount;

    @Schema(description = "打赏留言")
    private String message;
}
