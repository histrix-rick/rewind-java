package com.rewindai.admin.dto;

import com.rewindai.system.storage.enums.BucketAccessType;
import com.rewindai.system.storage.enums.StorageProvider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 存储配置请求 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StorageConfigRequest {

    @NotBlank(message = "配置key不能为空")
    private String configKey;

    @NotBlank(message = "访问站点不能为空")
    private String accessEndpoint;

    private String customDomain;

    @NotBlank(message = "accessKey不能为空")
    private String accessKey;

    @NotBlank(message = "secretKey不能为空")
    private String secretKey;

    @NotBlank(message = "桶名称不能为空")
    private String bucketName;

    private String pathPrefix;

    private Boolean isHttps;

    private BucketAccessType bucketAccessType;

    private String region;

    @NotNull(message = "存储服务商不能为空")
    private StorageProvider provider;

    private Boolean isDefault;

    private String remark;
}
