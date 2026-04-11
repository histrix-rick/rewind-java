package com.rewindai.admin.dto;

import com.rewindai.system.security.entity.RiskRule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * 风控规则响应 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskRuleResponse {

    private Long id;
    private String ruleName;
    private String ruleCode;
    private String riskType;
    private String riskLevel;
    private String ruleConfig;
    private String description;
    private String status;
    private Integer sortOrder;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public static RiskRuleResponse from(RiskRule rule) {
        return RiskRuleResponse.builder()
                .id(rule.getId())
                .ruleName(rule.getRuleName())
                .ruleCode(rule.getRuleCode())
                .riskType(rule.getRiskType() != null ? rule.getRiskType().name() : null)
                .riskLevel(rule.getRiskLevel() != null ? rule.getRiskLevel().name() : null)
                .ruleConfig(rule.getRuleConfig())
                .description(rule.getDescription())
                .status(rule.getStatus() != null ? rule.getStatus().name() : null)
                .sortOrder(rule.getSortOrder())
                .createdAt(rule.getCreatedAt())
                .updatedAt(rule.getUpdatedAt())
                .build();
    }
}
