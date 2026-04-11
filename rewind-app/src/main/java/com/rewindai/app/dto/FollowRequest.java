package com.rewindai.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

/**
 * 关注请求 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Schema(description = "关注请求")
public class FollowRequest {

    @NotNull(message = "关注用户ID不能为空")
    @Schema(description = "要关注的用户ID", required = true)
    private UUID followingId;
}
