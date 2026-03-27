package com.rewindai.app.dto;

import com.rewindai.system.daydream.entity.DreamRelationship;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 梦境人物关系详情响应 DTO（包含关联信息）
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DreamRelationshipDetailResponse {

    private UUID id;
    private UUID dreamId;
    private UUID nodeId;
    private String personName;
    private Long relationshipTypeId;
    private RelationshipTypeResponse relationshipType;
    private Integer intimacyLevel;
    private String intimacyDescription;
    private String notes;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public static DreamRelationshipDetailResponse from(DreamRelationship relationship) {
        return DreamRelationshipDetailResponse.builder()
                .id(relationship.getId())
                .dreamId(relationship.getDreamId())
                .nodeId(relationship.getNodeId())
                .personName(relationship.getPersonName())
                .relationshipTypeId(relationship.getRelationshipTypeId())
                .intimacyLevel(relationship.getIntimacyLevel())
                .intimacyDescription(relationship.getIntimacyDescription())
                .notes(relationship.getNotes())
                .createdAt(relationship.getCreatedAt())
                .updatedAt(relationship.getUpdatedAt())
                .build();
    }
}
