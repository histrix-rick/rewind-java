package com.rewindai.admin.dto;

import com.rewindai.system.config.entity.SysConfig;
import com.rewindai.system.config.enums.ConfigCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * 系统配置响应 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysConfigResponse {

    private Long id;
    private String configKey;
    private String configName;
    private String configValue;
    private ConfigCategory configCategory;
    private String valueType;
    private String description;
    private Boolean isEncrypted;
    private Integer sortOrder;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public static SysConfigResponse from(SysConfig config) {
        return SysConfigResponse.builder()
                .id(config.getId())
                .configKey(config.getConfigKey())
                .configName(config.getConfigName())
                .configValue(Boolean.TRUE.equals(config.getIsEncrypted()) ? maskValue(config.getConfigValue()) : config.getConfigValue())
                .configCategory(config.getConfigCategory())
                .valueType(config.getValueType())
                .description(config.getDescription())
                .isEncrypted(config.getIsEncrypted())
                .sortOrder(config.getSortOrder())
                .createdAt(config.getCreatedAt())
                .updatedAt(config.getUpdatedAt())
                .build();
    }

    private static String maskValue(String value) {
        if (value == null || value.length() <= 4) {
            return "****";
        }
        return value.substring(0, 2) + "****" + value.substring(value.length() - 2);
    }
}
