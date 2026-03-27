package com.rewindai.system.daydream.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 梦境人物关系实体
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dream_relationships", indexes = {
        @Index(name = "idx_rel_dream_id", columnList = "dream_id"),
        @Index(name = "idx_rel_node_id", columnList = "node_id")
})
public class DreamRelationship {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "dream_id", nullable = false)
    private UUID dreamId;

    @Column(name = "node_id")
    private UUID nodeId;

    @Column(name = "person_name", nullable = false, length = 100)
    private String personName;

    @Column(name = "relationship_type_id", nullable = false)
    private Long relationshipTypeId;

    @Column(name = "intimacy_level", nullable = false)
    @Builder.Default
    private Integer intimacyLevel = 1;

    @Column(name = "intimacy_description", length = 100)
    private String intimacyDescription;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
