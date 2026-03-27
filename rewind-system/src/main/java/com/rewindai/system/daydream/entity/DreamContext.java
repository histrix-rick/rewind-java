package com.rewindai.system.daydream.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 梦境上下文实体（关联时间轴节点）
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dream_contexts", indexes = {
        @Index(name = "idx_ctx_dream_id", columnList = "dream_id"),
        @Index(name = "idx_ctx_node_id", columnList = "node_id")
})
public class DreamContext {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "dream_id", nullable = false)
    private UUID dreamId;

    @Column(name = "node_id")
    private UUID nodeId;

    @Column(name = "identity_id")
    private Long identityId;

    @Column(name = "financial_amount", nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal financialAmount = BigDecimal.ZERO;

    @Column(name = "education_level_id")
    private Long educationLevelId;

    @Column(name = "birth_province", length = 50)
    private String birthProvince;

    @Column(name = "birth_city", length = 50)
    private String birthCity;

    @Column(name = "birth_district", length = 50)
    private String birthDistrict;

    @Column(name = "birth_address", columnDefinition = "TEXT")
    private String birthAddress;

    @Column(name = "dream_province", length = 50)
    private String dreamProvince;

    @Column(name = "dream_city", length = 50)
    private String dreamCity;

    @Column(name = "dream_district", length = 50)
    private String dreamDistrict;

    @Column(name = "dream_address", columnDefinition = "TEXT")
    private String dreamAddress;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
