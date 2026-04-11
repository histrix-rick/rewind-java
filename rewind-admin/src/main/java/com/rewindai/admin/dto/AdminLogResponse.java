package com.rewindai.admin.dto;

import com.rewindai.system.security.entity.AdminOperationLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * 管理员操作日志响应 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminLogResponse {

    private Long id;
    private Integer adminId;
    private String adminUsername;
    private String operationType;
    private String module;
    private String description;
    private String requestMethod;
    private String requestUrl;
    private String requestParams;
    private Integer responseStatus;
    private String clientIp;
    private String userAgent;
    private Integer executionTime;
    private OffsetDateTime createdAt;

    public static AdminLogResponse from(AdminOperationLog log) {
        return AdminLogResponse.builder()
                .id(log.getId())
                .adminId(log.getAdminId())
                .adminUsername(log.getAdminUsername())
                .operationType(log.getOperationType())
                .module(log.getModule())
                .description(log.getDescription())
                .requestMethod(log.getRequestMethod())
                .requestUrl(log.getRequestUrl())
                .requestParams(log.getRequestParams())
                .responseStatus(log.getResponseStatus())
                .clientIp(log.getClientIp())
                .userAgent(log.getUserAgent())
                .executionTime(log.getExecutionTime())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
