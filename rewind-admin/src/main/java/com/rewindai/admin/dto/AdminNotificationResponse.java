package com.rewindai.admin.dto;

import com.rewindai.system.notification.entity.Notification;
import com.rewindai.system.notification.enums.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 后台管理 - 通知响应DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "后台通知响应")
public class AdminNotificationResponse {

    @Schema(description = "通知ID")
    private UUID id;

    @Schema(description = "用户ID")
    private UUID userId;

    @Schema(description = "用户昵称")
    private String userNickname;

    @Schema(description = "通知类型")
    private NotificationType type;

    @Schema(description = "类型名称")
    private String typeName;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "关联ID")
    private UUID relatedId;

    @Schema(description = "关联类型")
    private String relatedType;

    @Schema(description = "是否已读")
    private Boolean isRead;

    @Schema(description = "创建时间")
    private OffsetDateTime createdAt;

    public static AdminNotificationResponse fromEntity(Notification notification) {
        return AdminNotificationResponse.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .type(notification.getType())
                .typeName(notification.getType() != null ? notification.getType().getDesc() : null)
                .title(notification.getTitle())
                .content(notification.getContent())
                .relatedId(notification.getRelatedId())
                .relatedType(notification.getRelatedType())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
