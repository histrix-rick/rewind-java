package com.rewindai.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 后台管理 - 梦境打赏响应 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "后台梦境打赏响应")
public class AdminDreamRewardResponse {

    @Schema(description = "打赏记录ID")
    private UUID id;

    @Schema(description = "梦境ID")
    private UUID dreamId;

    @Schema(description = "梦境标题")
    private String dreamTitle;

    @Schema(description = "打赏者ID")
    private UUID senderId;

    @Schema(description = "打赏者昵称")
    private String senderNickname;

    @Schema(description = "打赏者头像")
    private String senderAvatar;

    @Schema(description = "接收者ID")
    private UUID receiverId;

    @Schema(description = "接收者昵称")
    private String receiverNickname;

    @Schema(description = "接收者头像")
    private String receiverAvatar;

    @Schema(description = "打赏金额")
    private BigDecimal amount;

    @Schema(description = "打赏留言")
    private String message;

    @Schema(description = "打赏时间")
    private OffsetDateTime createdAt;
}
