package com.rewindai.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 风控规则请求 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskRuleRequest {

    @NotBlank(message = "规则名称不能为空")
    private String ruleName;

    @NotBlank(message = "规则编码不能为空")
    private String ruleCode;

    private String riskType;

    private String riskLevel;

    private String ruleConfig;

    private String description;

    private String status;

    private Integer sortOrder;
}
