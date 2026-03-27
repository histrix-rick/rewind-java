package com.rewindai.app.dto;

import com.rewindai.system.daydream.entity.DreamContext;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 梦境上下文详情响应 DTO（包含关联信息）
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DreamContextDetailResponse {

    private UUID id;
    private UUID dreamId;
    private UUID nodeId;
    private Long identityId;
    private UserIdentityResponse identity;
    private BigDecimal financialAmount;
    private Long educationLevelId;
    private EducationLevelResponse educationLevel;
    private String birthProvince;
    private String birthCity;
    private String birthDistrict;
    private String birthAddress;
    private String dreamProvince;
    private String dreamCity;
    private String dreamDistrict;
    private String dreamAddress;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public static DreamContextDetailResponse from(DreamContext context) {
        return DreamContextDetailResponse.builder()
                .id(context.getId())
                .dreamId(context.getDreamId())
                .nodeId(context.getNodeId())
                .identityId(context.getIdentityId())
                .financialAmount(context.getFinancialAmount())
                .educationLevelId(context.getEducationLevelId())
                .birthProvince(context.getBirthProvince())
                .birthCity(context.getBirthCity())
                .birthDistrict(context.getBirthDistrict())
                .birthAddress(context.getBirthAddress())
                .dreamProvince(context.getDreamProvince())
                .dreamCity(context.getDreamCity())
                .dreamDistrict(context.getDreamDistrict())
                .dreamAddress(context.getDreamAddress())
                .createdAt(context.getCreatedAt())
                .updatedAt(context.getUpdatedAt())
                .build();
    }
}
