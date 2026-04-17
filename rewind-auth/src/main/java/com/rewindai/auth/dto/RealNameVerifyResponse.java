package com.rewindai.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 实名认证验证响应DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "实名认证验证响应")
public class RealNameVerifyResponse {

    @Schema(description = "是否验证通过", required = true)
    private Boolean passed;

    @Schema(description = "验证消息")
    private String message;

    @Schema(description = "性别 0-未知 1-男 2-女", example = "1")
    private Integer gender;

    @Schema(description = "出生日期", example = "1990-01-01")
    private String birthDate;
}
