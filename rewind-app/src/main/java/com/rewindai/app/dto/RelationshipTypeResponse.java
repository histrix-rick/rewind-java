package com.rewindai.app.dto;

import com.rewindai.system.daydream.entity.RelationshipType;
import com.rewindai.system.daydream.enums.RelationshipCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * 关系类型响应 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelationshipTypeResponse {

    private Long id;
    private String name;
    private String description;
    private RelationshipCategory category;
    private Integer sortOrder;
    private Boolean isActive;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public static RelationshipTypeResponse from(RelationshipType type) {
        return RelationshipTypeResponse.builder()
                .id(type.getId())
                .name(type.getName())
                .description(type.getDescription())
                .category(type.getCategory())
                .sortOrder(type.getSortOrder())
                .isActive(type.getIsActive())
                .createdAt(type.getCreatedAt())
                .updatedAt(type.getUpdatedAt())
                .build();
    }
}
