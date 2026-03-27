package com.rewindai.app.dto;

import com.rewindai.system.user.entity.UserAttribute;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 用户属性响应 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAttributeResponse {

    private UUID id;
    private UUID userId;
    private Integer financialPower;
    private Integer intelligence;
    private Integer physicalPower;
    private Integer charisma;
    private Integer luck;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public static UserAttributeResponse from(UserAttribute attribute) {
        return UserAttributeResponse.builder()
                .id(attribute.getId())
                .userId(attribute.getUserId())
                .financialPower(attribute.getFinancialPower())
                .intelligence(attribute.getIntelligence())
                .physicalPower(attribute.getPhysicalPower())
                .charisma(attribute.getCharisma())
                .luck(attribute.getLuck())
                .createdAt(attribute.getCreatedAt())
                .updatedAt(attribute.getUpdatedAt())
                .build();
    }
}
