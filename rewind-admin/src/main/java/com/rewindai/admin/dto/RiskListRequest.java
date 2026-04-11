package com.rewindai.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * 风险名单请求 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskListRequest {

    @NotBlank(message = "名单类型不能为空")
    private String listType;

    @NotBlank(message = "目标值不能为空")
    private String targetValue;

    private String riskLevel;

    private String reason;

    private Integer addedBy;

    private OffsetDateTime expiresAt;
}
