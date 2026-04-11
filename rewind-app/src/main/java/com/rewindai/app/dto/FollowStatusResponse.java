package com.rewindai.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 关注状态响应 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "关注状态响应")
public class FollowStatusResponse {

    @Schema(description = "是否已关注")
    private Boolean isFollowing;

    @Schema(description = "关注者数量（粉丝数）")
    private Long followerCount;

    @Schema(description = "关注数量")
    private Long followingCount;
}
