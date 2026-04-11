package com.rewindai.admin.dto;

import com.rewindai.system.security.entity.UserSensitiveLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 用户敏感操作日志响应 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLogResponse {

    private Long id;
    private UUID userId;
    private String operationType;
    private String description;
    private String clientIp;
    private String deviceInfo;
    private String location;
    private String riskLevel;
    private OffsetDateTime createdAt;

    public static UserLogResponse from(UserSensitiveLog log) {
        return UserLogResponse.builder()
                .id(log.getId())
                .userId(log.getUserId())
                .operationType(log.getOperationType())
                .description(log.getDescription())
                .clientIp(log.getClientIp())
                .deviceInfo(log.getDeviceInfo())
                .location(log.getLocation())
                .riskLevel(log.getRiskLevel() != null ? log.getRiskLevel().name() : null)
                .createdAt(log.getCreatedAt())
                .build();
    }
}
