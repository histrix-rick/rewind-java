package com.rewindai.system.daydream.entity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 时间轴节点扩展实体
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "timeline_node_extensions", indexes = {
        @Index(name = "idx_tne_node_id", columnList = "node_id", unique = true)
})
public class TimelineNodeExtension {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "node_id", nullable = false, unique = true)
    private UUID nodeId;

    @Column(name = "asset_info_completed", nullable = false)
    @Builder.Default
    private Boolean assetInfoCompleted = false;

    @Column(name = "attribute_updates_json", columnDefinition = "TEXT")
    private String attributeUpdatesJson;

    @Column(name = "relationship_updates_json", columnDefinition = "TEXT")
    private String relationshipUpdatesJson;

    @Column(name = "identity_updates_json", columnDefinition = "TEXT")
    private String identityUpdatesJson;

    @Column(name = "ai_reasoning_trace", columnDefinition = "TEXT")
    private String aiReasoningTrace;

    @Transient
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> getAttributeUpdates() {
        try {
            if (attributeUpdatesJson == null || attributeUpdatesJson.isEmpty()) {
                return new HashMap<>();
            }
            return objectMapper.readValue(attributeUpdatesJson, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    public void setAttributeUpdates(Map<String, Object> updates) {
        try {
            this.attributeUpdatesJson = objectMapper.writeValueAsString(updates);
        } catch (Exception e) {
            this.attributeUpdatesJson = "{}";
        }
    }

    public Map<String, Object> getRelationshipUpdates() {
        try {
            if (relationshipUpdatesJson == null || relationshipUpdatesJson.isEmpty()) {
                return new HashMap<>();
            }
            return objectMapper.readValue(relationshipUpdatesJson, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    public void setRelationshipUpdates(Map<String, Object> updates) {
        try {
            this.relationshipUpdatesJson = objectMapper.writeValueAsString(updates);
        } catch (Exception e) {
            this.relationshipUpdatesJson = "{}";
        }
    }

    public Map<String, Object> getIdentityUpdates() {
        try {
            if (identityUpdatesJson == null || identityUpdatesJson.isEmpty()) {
                return new HashMap<>();
            }
            return objectMapper.readValue(identityUpdatesJson, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    public void setIdentityUpdates(Map<String, Object> updates) {
        try {
            this.identityUpdatesJson = objectMapper.writeValueAsString(updates);
        } catch (Exception e) {
            this.identityUpdatesJson = "{}";
        }
    }

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
