package com.rewindai.app.dto;

import com.rewindai.system.daydream.entity.UserIdentity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * 用户身份预设响应 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserIdentityResponse {

    private Long id;
    private String name;
    private String description;
    private Integer minAge;
    private Integer maxAge;
    private Integer sortOrder;
    private Boolean isActive;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public static UserIdentityResponse from(UserIdentity identity) {
        return UserIdentityResponse.builder()
                .id(identity.getId())
                .name(identity.getName())
                .description(identity.getDescription())
                .minAge(identity.getMinAge())
                .maxAge(identity.getMaxAge())
                .sortOrder(identity.getSortOrder())
                .isActive(identity.getIsActive())
                .createdAt(identity.getCreatedAt())
                .updatedAt(identity.getUpdatedAt())
                .build();
    }
}
