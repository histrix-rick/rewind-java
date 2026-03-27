package com.rewindai.app.dto;

import com.rewindai.system.daydream.entity.Daydream;
import com.rewindai.system.dream.enums.DreamPrivacy;
import com.rewindai.system.dream.enums.DreamStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 白日梦详情响应 DTO（包含上下文和关系）
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DaydreamDetailResponse {

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

    // 用户全局属性
    private UserAttributeResponse userAttribute;

    // 梦境上下文（初始配置）
    private DreamContextDetailResponse context;

    // 社会关系列表
    private List<DreamRelationshipDetailResponse> relationships;

    public static DaydreamDetailResponse from(Daydream daydream, BigDecimal progress) {
        return DaydreamDetailResponse.builder()
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
                .build();
    }
}
