package com.rewindai.app.dto;

import com.rewindai.system.daydream.entity.Daydream;
import com.rewindai.system.daydream.enums.DreamPrivacy;
import com.rewindai.system.daydream.enums.DreamStatus;
import com.rewindai.system.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 白日梦响应 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DaydreamResponse {

    private UUID id;
    private UUID userId;
    private String title;
    private String description;
    private String coverUrl;
    private LocalDate startDate;
    private LocalDate currentDate;
    private UUID currentBranchId;
    private Boolean isActive;
    private Boolean isFinished;
    private Boolean isPublic;
    private DreamStatus status;
    private DreamPrivacy privacy;
    private Integer viewCount;
    private Integer likeCount;
    private Integer shareCount;
    private Integer commentCount;
    private BigDecimal rewardAmount;
    private BigDecimal progress;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private OffsetDateTime deletedAt;
    private OffsetDateTime authorUpdatedAt;
    private Boolean isLiked;
    private SimpleUserResponse author;

    public static DaydreamResponse from(Daydream daydream, BigDecimal progress) {
        return DaydreamResponse.builder()
                .id(daydream.getId())
                .userId(daydream.getUserId())
                .title(daydream.getTitle())
                .description(daydream.getDescription())
                .coverUrl(daydream.getCoverUrl())
                .startDate(daydream.getStartDate())
                .currentDate(daydream.getCurrentDate())
                .currentBranchId(daydream.getCurrentBranchId())
                .isActive(daydream.getIsActive())
                .isFinished(daydream.getIsFinished())
                .isPublic(daydream.getIsPublic())
                .status(daydream.getStatus())
                .privacy(daydream.getPrivacy())
                .viewCount(daydream.getViewCount())
                .likeCount(daydream.getLikeCount())
                .shareCount(daydream.getShareCount())
                .commentCount(daydream.getCommentCount())
                .rewardAmount(daydream.getRewardAmount())
                .progress(progress)
                .createdAt(daydream.getCreatedAt())
                .updatedAt(daydream.getUpdatedAt())
                .deletedAt(daydream.getDeletedAt())
                .authorUpdatedAt(daydream.getAuthorUpdatedAt())
                .build();
    }

    public static DaydreamResponse from(Daydream daydream, BigDecimal progress, Boolean isLiked) {
        DaydreamResponse response = from(daydream, progress);
        response.setIsLiked(isLiked);
        return response;
    }

    public static DaydreamResponse from(Daydream daydream, BigDecimal progress, User author, Boolean isLiked) {
        DaydreamResponse response = from(daydream, progress, author);
        response.setIsLiked(isLiked);
        return response;
    }

    public static DaydreamResponse from(Daydream daydream, BigDecimal progress, User author) {
        DaydreamResponse response = from(daydream, progress);
        response.setAuthor(SimpleUserResponse.from(author));
        return response;
    }
}
