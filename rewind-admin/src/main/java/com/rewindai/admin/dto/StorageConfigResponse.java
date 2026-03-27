package com.rewindai.admin.dto;

import com.rewindai.system.storage.entity.StorageConfig;
import com.rewindai.system.storage.enums.BucketAccessType;
import com.rewindai.system.storage.enums.StorageProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * 存储配置响应 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StorageConfigResponse {

    private Long id;
    private String configKey;
    private String accessEndpoint;
    private String customDomain;
    private String accessKey;
    private String secretKey;
    private String bucketName;
    private String pathPrefix;
    private Boolean isHttps;
    private BucketAccessType bucketAccessType;
    private String region;
    private StorageProvider provider;
    private Boolean isDefault;
    private String remark;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public static StorageConfigResponse from(StorageConfig config) {
        return StorageConfigResponse.builder()
                .id(config.getId())
                .configKey(config.getConfigKey())
                .accessEndpoint(config.getAccessEndpoint())
                .customDomain(config.getCustomDomain())
                .accessKey(maskKey(config.getAccessKey()))
                .secretKey(maskSecret(config.getSecretKey()))
                .bucketName(config.getBucketName())
                .pathPrefix(config.getPathPrefix())
                .isHttps(config.getIsHttps())
                .bucketAccessType(config.getBucketAccessType())
                .region(config.getRegion())
                .provider(config.getProvider())
                .isDefault(config.getIsDefault())
                .remark(config.getRemark())
                .createdAt(config.getCreatedAt())
                .updatedAt(config.getUpdatedAt())
                .build();
    }

    private static String maskKey(String key) {
        if (key == null || key.length() <= 4) {
            return "****";
        }
        return key.substring(0, 4) + "****";
    }

    private static String maskSecret(String secret) {
        if (secret == null || secret.length() <= 4) {
            return "****";
        }
        return "****" + secret.substring(secret.length() - 4);
    }
}
