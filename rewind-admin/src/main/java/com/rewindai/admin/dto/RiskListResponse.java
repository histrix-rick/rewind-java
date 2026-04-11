package com.rewindai.admin.dto;

import com.rewindai.system.security.entity.RiskList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * 风险名单响应 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskListResponse {

    private Long id;
    private String listType;
    private String targetValue;
    private String riskLevel;
    private String reason;
    private Integer addedBy;
    private OffsetDateTime expiresAt;
    private OffsetDateTime createdAt;

    public static RiskListResponse from(RiskList list) {
        return RiskListResponse.builder()
                .id(list.getId())
                .listType(list.getListType())
                .targetValue(list.getTargetValue())
                .riskLevel(list.getRiskLevel() != null ? list.getRiskLevel().name() : null)
                .reason(list.getReason())
                .addedBy(list.getAddedBy())
                .expiresAt(list.getExpiresAt())
                .createdAt(list.getCreatedAt())
                .build();
    }
}
