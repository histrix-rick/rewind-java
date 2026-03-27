package com.rewindai.system.dream.entity;

import com.rewindai.system.dream.enums.DreamPrivacy;
import com.rewindai.system.dream.enums.DreamStatus;
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
 * 梦境实体
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dreams", indexes = {
        @Index(name = "idx_dreams_user_id", columnList = "user_id"),
        @Index(name = "idx_dreams_status", columnList = "status"),
        @Index(name = "idx_dreams_privacy", columnList = "privacy"),
        @Index(name = "idx_dreams_created_at", columnList = "created_at")
})
public class Dream {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "cover_url", columnDefinition = "TEXT")
    private String coverUrl;

    @Column(name = "dream_date")
    private OffsetDateTime dreamDate;

    @Column(name = "status", nullable = false)
    @Convert(converter = DreamStatusConverter.class)
    private DreamStatus status;

    @Column(name = "privacy", nullable = false)
    @Convert(converter = DreamPrivacyConverter.class)
    private DreamPrivacy privacy;

    @Column(name = "is_public", nullable = false)
    @Builder.Default
    private Boolean isPublic = false;

    @Column(name = "view_count", nullable = false)
    @Builder.Default
    private Integer viewCount = 0;

    @Column(name = "like_count", nullable = false)
    @Builder.Default
    private Integer likeCount = 0;

    @Column(name = "share_count", nullable = false)
    @Builder.Default
    private Integer shareCount = 0;

    @Column(name = "comment_count", nullable = false)
    @Builder.Default
    private Integer commentCount = 0;

    @Column(name = "reward_amount", nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal rewardAmount = BigDecimal.ZERO;

    @Column(name = "tags", length = 500)
    private String tags;

    @Column(name = "mood", length = 50)
    private String mood;

    @Column(name = "weather", length = 50)
    private String weather;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "is_lucid")
    @Builder.Default
    private Boolean isLucid = false;

    @Column(name = "is_recurring")
    @Builder.Default
    private Boolean isRecurring = false;

    @Column(name = "is_nightmare")
    @Builder.Default
    private Boolean isNightmare = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = DreamStatus.ACTIVE;
        }
        if (this.privacy == null) {
            this.privacy = DreamPrivacy.PRIVATE;
        }
        if (this.isPublic == null) {
            this.isPublic = false;
        }
    }

    @Converter
    public static class DreamStatusConverter implements AttributeConverter<DreamStatus, Integer> {
        @Override
        public Integer convertToDatabaseColumn(DreamStatus status) {
            return status != null ? status.getCode() : DreamStatus.ACTIVE.getCode();
        }

        @Override
        public DreamStatus convertToEntityAttribute(Integer code) {
            return code != null ? DreamStatus.fromCode(code) : DreamStatus.ACTIVE;
        }
    }

    @Converter
    public static class DreamPrivacyConverter implements AttributeConverter<DreamPrivacy, Integer> {
        @Override
        public Integer convertToDatabaseColumn(DreamPrivacy privacy) {
            return privacy != null ? privacy.getCode() : DreamPrivacy.PRIVATE.getCode();
        }

        @Override
        public DreamPrivacy convertToEntityAttribute(Integer code) {
            return code != null ? DreamPrivacy.fromCode(code) : DreamPrivacy.PRIVATE;
        }
    }
}
