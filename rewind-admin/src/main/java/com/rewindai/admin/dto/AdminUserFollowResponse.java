package com.rewindai.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 后台管理 - 用户关注关系响应 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "后台用户关注关系响应")
public class AdminUserFollowResponse {

    @Schema(description = "关注记录ID")
    private UUID id;

    @Schema(description = "关注者ID")
    private UUID followerId;

    @Schema(description = "关注者昵称")
    private String followerNickname;

    @Schema(description = "关注者头像")
    private String followerAvatar;

    @Schema(description = "被关注者ID")
    private UUID followingId;

    @Schema(description = "被关注者昵称")
    private String followingNickname;

    @Schema(description = "被关注者头像")
    private String followingAvatar;

    @Schema(description = "关注时间")
    private OffsetDateTime createdAt;
}
