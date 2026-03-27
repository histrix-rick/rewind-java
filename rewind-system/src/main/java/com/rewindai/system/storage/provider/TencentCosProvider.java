package com.rewindai.system.storage.provider;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.region.Region;
import com.rewindai.system.storage.entity.StorageConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 腾讯云 COS 存储提供者
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Component
public class TencentCosProvider implements CloudStorageProvider {

    @Override
    public String uploadFile(StorageConfig config, MultipartFile file, String filePath) throws IOException {
        COSClient cosClient = createCosClient(config);
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            PutObjectRequest request = new PutObjectRequest(
                    config.getBucketName(),
                    filePath,
                    file.getInputStream(),
                    metadata
            );

            cosClient.putObject(request);
            log.info("腾讯云COS文件上传成功: bucket={}, path={}", config.getBucketName(), filePath);

            return generateFileUrl(config, filePath);
        } finally {
            cosClient.shutdown();
        }
    }

    @Override
    public void deleteFile(StorageConfig config, String filePath) {
        COSClient cosClient = createCosClient(config);
        try {
            cosClient.deleteObject(config.getBucketName(), filePath);
            log.info("腾讯云COS文件删除成功: bucket={}, path={}", config.getBucketName(), filePath);
        } finally {
            cosClient.shutdown();
        }
    }

    private COSClient createCosClient(StorageConfig config) {
        COSCredentials cred = new BasicCOSCredentials(config.getAccessKey(), config.getSecretKey());

        String regionStr = config.getRegion();
        if (regionStr == null || regionStr.trim().isEmpty()) {
            // 如果region为空，尝试从accessEndpoint推断
            String endpoint = config.getAccessEndpoint();
            if (endpoint != null && endpoint.contains("cos.")) {
                // 尝试从endpoint提取region（例如 cos.ap-guangzhou.myqcloud.com -> ap-guangzhou）
                // 对于accelerate endpoint，使用一个默认region或者不设置region
                log.warn("Region为空，使用endpoint: {}, 将使用默认region配置", endpoint);
            }
            // 使用一个默认region作为fallback（COS SDK需要region）
            // 实际上，如果使用global加速endpoint，region可以是任意有效值
            regionStr = "ap-guangzhou";
            log.info("Region为空，使用默认region: {}", regionStr);
        }

        Region region = new Region(regionStr);
        ClientConfig clientConfig = new ClientConfig(region);

        if (Boolean.TRUE.equals(config.getIsHttps())) {
            clientConfig.setHttpProtocol(HttpProtocol.https);
        } else {
            clientConfig.setHttpProtocol(HttpProtocol.http);
        }

        log.info("创建COSClient: region={}, endpointBucket={}", regionStr, config.getBucketName());
        return new COSClient(cred, clientConfig);
    }

    private String generateFileUrl(StorageConfig config, String filePath) {
        String protocol = Boolean.TRUE.equals(config.getIsHttps()) ? "https" : "http";
        String domain;
        if (config.getCustomDomain() != null && !config.getCustomDomain().isEmpty()) {
            domain = normalizeDomain(config.getCustomDomain());
        } else {
            String bucketName = config.getBucketName().trim();
            String endpoint = normalizeDomain(config.getAccessEndpoint());

            // 判断：如果endpoint已经包含bucketName，则不重复添加
            if (endpoint.startsWith(bucketName + ".")) {
                domain = endpoint;
            } else {
                domain = bucketName + "." + endpoint;
            }
        }

        // 最终确保domain中没有双点
        domain = domain.replaceAll("\\.+", ".");

        String result = protocol + "://" + domain + "/" + filePath;
        log.info("生成文件URL: bucket={}, endpoint={}, customDomain={}, result={}",
                config.getBucketName(), config.getAccessEndpoint(), config.getCustomDomain(), result);
        return result;
    }

    /**
     * 标准化域名：移除前后空格、前后导点、替换双点为单点
     */
    private String normalizeDomain(String domain) {
        if (domain == null) {
            return "";
        }
        String result = domain.trim();
        // 移除前导点
        while (result.startsWith(".")) {
            result = result.substring(1);
        }
        // 移除尾随点
        while (result.endsWith(".")) {
            result = result.substring(0, result.length() - 1);
        }
        // 替换多个连续点为单个点
        result = result.replaceAll("\\.+", ".");
        return result;
    }
}

