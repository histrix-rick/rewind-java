package com.rewindai.system.wallet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * 奖励配置实体
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reward_configs", indexes = {
        @Index(name = "idx_reward_config_type", columnList = "reward_type"),
        @Index(name = "idx_reward_config_active", columnList = "is_active")
})
public class RewardConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reward_type", nullable = false, length = 50)
    private String rewardType;

    @Column(name = "reward_name", nullable = false, length = 100)
    private String rewardName;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "reward_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal rewardAmount;

    @Column(name = "daily_limit")
    private Integer dailyLimit;

    @Column(name = "total_limit")
    private Integer totalLimit;

    @Column(name = "min_level")
    private Integer minLevel;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
