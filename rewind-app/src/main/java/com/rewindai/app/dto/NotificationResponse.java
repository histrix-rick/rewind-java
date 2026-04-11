package com.rewindai.app.dto;

import com.rewindai.system.notification.entity.Notification;
import com.rewindai.system.notification.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 通知响应 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private UUID id;
    private UUID userId;
    private String type;
    private String typeDesc;
    private String title;
    private String content;
    private UUID relatedId;
    private String relatedType;
    private Boolean isRead;
    private OffsetDateTime createdAt;

    public static NotificationResponse from(Notification notification) {
        NotificationType type = notification.getType();
        return NotificationResponse.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .type(type.getCode())
                .typeDesc(type.getDesc())
                .title(notification.getTitle())
                .content(notification.getContent())
                .relatedId(notification.getRelatedId())
                .relatedType(notification.getRelatedType())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
