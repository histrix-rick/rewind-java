package com.rewindai.admin.dto;

import com.rewindai.system.daydream.entity.DreamComment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 后台管理 - 评论详情响应DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "后台评论详情响应")
public class AdminCommentResponse {

    @Schema(description = "评论ID")
    private UUID id;

    @Schema(description = "梦境ID")
    private UUID dreamId;

    @Schema(description = "用户ID")
    private UUID userId;

    @Schema(description = "用户昵称")
    private String userNickname;

    @Schema(description = "用户头像")
    private String userAvatar;

    @Schema(description = "父评论ID")
    private UUID parentCommentId;

    @Schema(description = "评论内容")
    private String content;

    @Schema(description = "点赞数")
    private Integer likeCount;

    @Schema(description = "是否已删除")
    private Boolean isDeleted;

    @Schema(description = "创建时间")
    private OffsetDateTime createdAt;

    @Schema(description = "更新时间")
    private OffsetDateTime updatedAt;

    /**
     * 从实体转换
     */
    public static AdminCommentResponse fromEntity(DreamComment comment) {
        return AdminCommentResponse.builder()
                .id(comment.getId())
                .dreamId(comment.getDreamId())
                .userId(comment.getUserId())
                .parentCommentId(comment.getParentCommentId())
                .content(comment.getContent())
                .likeCount(comment.getLikeCount())
                .isDeleted(comment.getIsDeleted())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
