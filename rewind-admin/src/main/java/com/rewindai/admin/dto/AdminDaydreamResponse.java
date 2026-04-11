package com.rewindai.admin.dto;

import com.rewindai.system.daydream.entity.Daydream;
import com.rewindai.system.daydream.enums.DreamStatus;
import com.rewindai.system.daydream.enums.ReviewStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 后台管理 - 白日梦详情响应DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "后台白日梦详情响应")
public class AdminDaydreamResponse {

    @Schema(description = "白日梦ID")
    private UUID id;

    @Schema(description = "用户ID")
    private UUID userId;

    @Schema(description = "用户昵称")
    private String userNickname;

    @Schema(description = "用户头像")
    private String userAvatar;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "封面URL")
    private String coverUrl;

    @Schema(description = "起始日期")
    private LocalDate startDate;

    @Schema(description = "当前日期")
    private LocalDate currentDate;

    @Schema(description = "是否活跃")
    private Boolean isActive;

    @Schema(description = "是否已结束")
    private Boolean isFinished;

    @Schema(description = "是否公开")
    private Boolean isPublic;

    @Schema(description = "状态")
    private DreamStatus status;

    @Schema(description = "审核状态")
    private ReviewStatus reviewStatus;

    @Schema(description = "是否精选")
    private Boolean isFeatured;

    @Schema(description = "是否置顶")
    private Boolean isPinned;

    @Schema(description = "置顶时间")
    private OffsetDateTime pinnedAt;

    @Schema(description = "精选时间")
    private OffsetDateTime featuredAt;

    @Schema(description = "审核时间")
    private OffsetDateTime reviewedAt;

    @Schema(description = "审核人ID")
    private UUID reviewedBy;

    @Schema(description = "审核原因")
    private String reviewReason;

    @Schema(description = "浏览数")
    private Integer viewCount;

    @Schema(description = "点赞数")
    private Integer likeCount;

    @Schema(description = "分享数")
    private Integer shareCount;

    @Schema(description = "评论数")
    private Integer commentCount;

    @Schema(description = "打赏金额")
    private BigDecimal rewardAmount;

    @Schema(description = "创建时间")
    private OffsetDateTime createdAt;

    @Schema(description = "更新时间")
    private OffsetDateTime updatedAt;

    @Schema(description = "删除时间")
    private OffsetDateTime deletedAt;

    /**
     * 从实体转换
     */
    public static AdminDaydreamResponse fromEntity(Daydream daydream) {
        return AdminDaydreamResponse.builder()
                .id(daydream.getId())
                .userId(daydream.getUserId())
                .title(daydream.getTitle())
                .description(daydream.getDescription())
                .coverUrl(daydream.getCoverUrl())
                .startDate(daydream.getStartDate())
                .currentDate(daydream.getCurrentDate())
                .isActive(daydream.getIsActive())
                .isFinished(daydream.getIsFinished())
                .isPublic(daydream.getIsPublic())
                .status(daydream.getStatus())
                .reviewStatus(daydream.getReviewStatus())
                .isFeatured(daydream.getIsFeatured())
                .isPinned(daydream.getIsPinned())
                .pinnedAt(daydream.getPinnedAt())
                .featuredAt(daydream.getFeaturedAt())
                .reviewedAt(daydream.getReviewedAt())
                .reviewedBy(daydream.getReviewedBy())
                .reviewReason(daydream.getReviewReason())
                .viewCount(daydream.getViewCount())
                .likeCount(daydream.getLikeCount())
                .shareCount(daydream.getShareCount())
                .commentCount(daydream.getCommentCount())
                .rewardAmount(daydream.getRewardAmount())
                .createdAt(daydream.getCreatedAt())
                .updatedAt(daydream.getUpdatedAt())
                .deletedAt(daydream.getDeletedAt())
                .build();
    }
}
