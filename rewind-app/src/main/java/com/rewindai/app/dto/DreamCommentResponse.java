package com.rewindai.app.dto;

import com.rewindai.system.daydream.entity.DreamComment;
import com.rewindai.system.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 梦境评论响应 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DreamCommentResponse {

    private UUID id;
    private UUID dreamId;
    private UUID userId;
    private UUID parentCommentId;
    private String content;
    private Integer likeCount;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    // 评论用户信息
    private String username;
    private String nickname;
    private String avatarUrl;

    // 回复列表（用于嵌套展示）
    private List<DreamCommentResponse> replies;

    public static DreamCommentResponse from(DreamComment comment) {
        return DreamCommentResponse.builder()
                .id(comment.getId())
                .dreamId(comment.getDreamId())
                .userId(comment.getUserId())
                .parentCommentId(comment.getParentCommentId())
                .content(comment.getContent())
                .likeCount(comment.getLikeCount())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    public static DreamCommentResponse from(DreamComment comment, User user) {
        return DreamCommentResponse.builder()
                .id(comment.getId())
                .dreamId(comment.getDreamId())
                .userId(comment.getUserId())
                .parentCommentId(comment.getParentCommentId())
                .content(comment.getContent())
                .likeCount(comment.getLikeCount())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }
}
