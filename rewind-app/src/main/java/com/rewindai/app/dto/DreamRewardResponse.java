package com.rewindai.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 梦境打赏响应 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "梦境打赏响应")
public class DreamRewardResponse {

    @Schema(description = "打赏记录ID")
    private UUID id;

    @Schema(description = "梦境ID")
    private UUID dreamId;

    @Schema(description = "打赏者ID")
    private UUID senderId;

    @Schema(description = "打赏者昵称")
    private String senderNickname;

    @Schema(description = "接收者ID")
    private UUID receiverId;

    @Schema(description = "打赏金额")
    private BigDecimal amount;

    @Schema(description = "打赏留言")
    private String message;

    @Schema(description = "创建时间")
    private OffsetDateTime createdAt;

    public static DreamRewardResponse from(com.rewindai.system.daydream.entity.DreamReward reward) {
        return DreamRewardResponse.builder()
                .id(reward.getId())
                .dreamId(reward.getDreamId())
                .senderId(reward.getSenderId())
                .receiverId(reward.getReceiverId())
                .amount(reward.getAmount())
                .message(reward.getMessage())
                .createdAt(reward.getCreatedAt())
                .build();
    }
}
