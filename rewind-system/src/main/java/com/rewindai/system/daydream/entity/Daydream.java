package com.rewindai.system.daydream.entity;

import com.rewindai.system.daydream.enums.DreamPrivacy;
import com.rewindai.system.daydream.enums.DreamStatus;
import com.rewindai.system.daydream.enums.ReviewStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 白日梦世界表实体
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dream_worlds", indexes = {
        @Index(name = "idx_dw_user_id", columnList = "user_id"),
        @Index(name = "idx_dw_active", columnList = "user_id, is_active"),
        @Index(name = "idx_dw_created_at", columnList = "created_at"),
        @Index(name = "idx_dw_review_status", columnList = "review_status"),
        @Index(name = "idx_dw_featured", columnList = "is_featured"),
        @Index(name = "idx_dw_pinned", columnList = "is_pinned")
})
public class Daydream {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "cover_url", columnDefinition = "TEXT")
    private String coverUrl;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "current_day", nullable = false)
    private LocalDate currentDate;

    @Column(name = "branch_root_id")
    private UUID branchRootId;

    @Column(name = "parent_branch_id")
    private UUID parentBranchId;

    @Column(name = "current_branch_id")
    private UUID currentBranchId;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_finished", nullable = false)
    @Builder.Default
    private Boolean isFinished = false;

    @Column(name = "is_public", nullable = false)
    @Builder.Default
    private Boolean isPublic = false;

    @Column(name = "status", nullable = false)
    @Convert(converter = DreamStatusConverter.class)
    private DreamStatus status;

    @Column(name = "privacy", nullable = false)
    @Convert(converter = DreamPrivacyConverter.class)
    private DreamPrivacy privacy;

    @Column(name = "view_count")
    @Builder.Default
    private Integer viewCount = 0;

    @Column(name = "like_count")
    @Builder.Default
    private Integer likeCount = 0;

    @Column(name = "share_count")
    @Builder.Default
    private Integer shareCount = 0;

    @Column(name = "comment_count")
    @Builder.Default
    private Integer commentCount = 0;

    @Column(name = "reward_amount", nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal rewardAmount = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "author_updated_at")
    private OffsetDateTime authorUpdatedAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    @Column(name = "review_status")
    @Convert(converter = ReviewStatusConverter.class)
    @Builder.Default
    private ReviewStatus reviewStatus = ReviewStatus.APPROVED;

    @Column(name = "is_featured")
    @Builder.Default
    private Boolean isFeatured = false;

    @Column(name = "is_pinned")
    @Builder.Default
    private Boolean isPinned = false;

    @Column(name = "pinned_at")
    private OffsetDateTime pinnedAt;

    @Column(name = "featured_at")
    private OffsetDateTime featuredAt;

    @Column(name = "reviewed_at")
    private OffsetDateTime reviewedAt;

    @Column(name = "reviewed_by")
    private UUID reviewedBy;

    @Column(name = "review_reason", length = 500)
    private String reviewReason;

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = DreamStatus.ACTIVE;
        }
        if (this.privacy == null) {
            this.privacy = DreamPrivacy.PRIVATE;
        }
        if (this.currentDate == null) {
            this.currentDate = this.startDate;
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

    @Converter
    public static class ReviewStatusConverter implements AttributeConverter<ReviewStatus, Integer> {
        @Override
        public Integer convertToDatabaseColumn(ReviewStatus status) {
            return status != null ? status.getCode() : ReviewStatus.APPROVED.getCode();
        }

        @Override
        public ReviewStatus convertToEntityAttribute(Integer code) {
            return code != null ? ReviewStatus.fromCode(code) : ReviewStatus.APPROVED;
        }
    }
}
