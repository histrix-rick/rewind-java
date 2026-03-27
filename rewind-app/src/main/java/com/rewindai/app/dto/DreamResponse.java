package com.rewindai.app.dto;

import com.rewindai.system.dream.entity.Dream;
import com.rewindai.system.dream.enums.DreamPrivacy;
import com.rewindai.system.dream.enums.DreamStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 梦境响应 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DreamResponse {

    private UUID id;
    private UUID userId;
    private String title;
    private String content;
    private String coverUrl;
    private OffsetDateTime dreamDate;
    private DreamStatus status;
    private DreamPrivacy privacy;
    private Boolean isPublic;
    private Integer viewCount;
    private Integer likeCount;
    private Integer shareCount;
    private Integer commentCount;
    private BigDecimal rewardAmount;
    private String tags;
    private String mood;
    private String weather;
    private Integer durationMinutes;
    private Boolean isLucid;
    private Boolean isRecurring;
    private Boolean isNightmare;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public static DreamResponse from(Dream dream) {
        return DreamResponse.builder()
                .id(dream.getId())
                .userId(dream.getUserId())
                .title(dream.getTitle())
                .content(dream.getContent())
                .coverUrl(dream.getCoverUrl())
                .dreamDate(dream.getDreamDate())
                .status(dream.getStatus())
                .privacy(dream.getPrivacy())
                .isPublic(dream.getIsPublic())
                .viewCount(dream.getViewCount())
                .likeCount(dream.getLikeCount())
                .shareCount(dream.getShareCount())
                .commentCount(dream.getCommentCount())
                .rewardAmount(dream.getRewardAmount())
                .tags(dream.getTags())
                .mood(dream.getMood())
                .weather(dream.getWeather())
                .durationMinutes(dream.getDurationMinutes())
                .isLucid(dream.getIsLucid())
                .isRecurring(dream.getIsRecurring())
                .isNightmare(dream.getIsNightmare())
                .createdAt(dream.getCreatedAt())
                .updatedAt(dream.getUpdatedAt())
                .build();
    }
}
