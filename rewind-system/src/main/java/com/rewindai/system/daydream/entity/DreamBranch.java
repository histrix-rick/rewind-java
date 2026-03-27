package com.rewindai.system.daydream.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 梦境分支表实体
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dream_branches", indexes = {
        @Index(name = "idx_db_dream_id", columnList = "dream_id")
})
public class DreamBranch {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "dream_id", nullable = false)
    private UUID dreamId;

    @Column(name = "parent_node_id")
    private UUID parentNodeId;

    @Column(name = "branch_name", length = 100)
    private String branchName;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
