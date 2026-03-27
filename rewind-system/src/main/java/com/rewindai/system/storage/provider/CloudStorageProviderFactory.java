package com.rewindai.system.storage.provider;

import com.rewindai.common.core.exception.BusinessException;
import com.rewindai.common.core.result.ErrorCode;
import com.rewindai.system.storage.entity.StorageConfig;
import com.rewindai.system.storage.enums.StorageProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 云存储提供者工厂
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CloudStorageProviderFactory {

    private final Map<String, CloudStorageProvider> providers;

    public CloudStorageProvider getProvider(StorageConfig config) {
        StorageProvider provider = config.getProvider();
        return switch (provider) {
            case TENCENT_COS -> providers.get("tencentCosProvider");
            case ALIYUN_OSS -> providers.get("aliyunOssProvider");
            case AWS_S3 -> providers.get("awsS3Provider");
            case MINIO -> providers.get("minioProvider");
            case QINIU_KODO -> providers.get("qiniuKodoProvider");
            case LOCAL -> providers.get("localStorageProvider");
            default -> throw new BusinessException(ErrorCode.BAD_REQUEST, "不支持的存储提供商: " + provider);
        };
    }
}

