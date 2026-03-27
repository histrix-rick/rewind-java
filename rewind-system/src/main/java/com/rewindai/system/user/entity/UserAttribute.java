package com.rewindai.system.user.entity;

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
 * 用户属性表实体
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_attributes", indexes = {
        @Index(name = "idx_attr_user_id", columnList = "user_id", unique = true)
})
public class UserAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "financial_power", nullable = false)
    @Builder.Default
    private Integer financialPower = 50;

    @Column(name = "intelligence", nullable = false)
    @Builder.Default
    private Integer intelligence = 50;

    @Column(name = "physical_power", nullable = false)
    @Builder.Default
    private Integer physicalPower = 50;

    @Column(name = "charisma", nullable = false)
    @Builder.Default
    private Integer charisma = 50;

    @Column(name = "luck", nullable = false)
    @Builder.Default
    private Integer luck = 50;

    @Version
    @Column(name = "version")
    @Builder.Default
    private Long version = 0L;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
