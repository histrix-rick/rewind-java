package com.rewindai.system.storage.service;

import com.rewindai.common.core.exception.BusinessException;
import com.rewindai.common.core.result.ErrorCode;
import com.rewindai.system.storage.entity.StorageConfig;
import com.rewindai.system.storage.enums.BucketAccessType;
import com.rewindai.system.storage.enums.StorageProvider;
import com.rewindai.system.storage.repository.StorageConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 存储配置 Service
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StorageConfigService {

    private final StorageConfigRepository storageConfigRepository;

    public Optional<StorageConfig> findById(Long id) {
        return storageConfigRepository.findById(id);
    }

    public Optional<StorageConfig> findByConfigKey(String configKey) {
        return storageConfigRepository.findByConfigKey(configKey);
    }

    public Optional<StorageConfig> getDefaultConfig() {
        return storageConfigRepository.findByIsDefaultTrue();
    }

    public List<StorageConfig> findAll() {
        return storageConfigRepository.findAll();
    }

    public List<StorageConfig> searchByConfigKey(String configKey) {
        return storageConfigRepository.findByConfigKeyContainingIgnoreCase(configKey);
    }

    public List<StorageConfig> searchByBucketName(String bucketName) {
        return storageConfigRepository.findByBucketNameContainingIgnoreCase(bucketName);
    }

    @Transactional
    public StorageConfig createConfig(StorageConfig config) {
        if (storageConfigRepository.findByConfigKey(config.getConfigKey()).isPresent()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "配置key已存在");
        }

        if (Boolean.TRUE.equals(config.getIsDefault())) {
            storageConfigRepository.clearDefault();
        }

        // 标准化处理accessEndpoint和customDomain
        normalizeEndpoint(config);

        StorageConfig saved = storageConfigRepository.save(config);
        log.info("存储配置创建成功: configKey={}", saved.getConfigKey());
        return saved;
    }

    @Transactional
    public StorageConfig updateConfig(Long id, StorageConfig update) {
        StorageConfig config = findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "配置不存在"));

        if (update.getConfigKey() != null && !update.getConfigKey().equals(config.getConfigKey())) {
            if (storageConfigRepository.findByConfigKey(update.getConfigKey()).isPresent()) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "配置key已存在");
            }
            config.setConfigKey(update.getConfigKey());
        }

        if (update.getAccessEndpoint() != null) {
            config.setAccessEndpoint(normalizeDomain(update.getAccessEndpoint()));
        }
        if (update.getCustomDomain() != null) {
            config.setCustomDomain(normalizeDomain(update.getCustomDomain()));
        }
        // 只有当accessKey不是脱敏格式时才更新
        if (update.getAccessKey() != null && !isMasked(update.getAccessKey())) {
            config.setAccessKey(update.getAccessKey());
        }
        // 只有当secretKey不是脱敏格式时才更新
        if (update.getSecretKey() != null && !isMasked(update.getSecretKey())) {
            config.setSecretKey(update.getSecretKey());
        }
        if (update.getBucketName() != null) {
            config.setBucketName(update.getBucketName());
        }
        if (update.getPathPrefix() != null) {
            config.setPathPrefix(update.getPathPrefix());
        }
        if (update.getIsHttps() != null) {
            config.setIsHttps(update.getIsHttps());
        }
        if (update.getBucketAccessType() != null) {
            config.setBucketAccessType(update.getBucketAccessType());
        }
        if (update.getRegion() != null) {
            config.setRegion(update.getRegion());
        }
        if (update.getProvider() != null) {
            config.setProvider(update.getProvider());
        }
        if (update.getRemark() != null) {
            config.setRemark(update.getRemark());
        }

        if (Boolean.TRUE.equals(update.getIsDefault()) && !Boolean.TRUE.equals(config.getIsDefault())) {
            storageConfigRepository.clearDefault();
            config.setIsDefault(true);
        } else if (Boolean.FALSE.equals(update.getIsDefault())) {
            config.setIsDefault(false);
        }

        StorageConfig saved = storageConfigRepository.save(config);
        log.info("存储配置更新成功: configKey={}", saved.getConfigKey());
        return saved;
    }

    @Transactional
    public void deleteConfig(Long id) {
        StorageConfig config = findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "配置不存在"));

        storageConfigRepository.delete(config);
        log.info("存储配置删除成功: configKey={}", config.getConfigKey());
    }

    @Transactional
    public StorageConfig setDefault(Long id) {
        StorageConfig config = findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "配置不存在"));

        storageConfigRepository.clearDefault();
        config.setIsDefault(true);

        StorageConfig saved = storageConfigRepository.save(config);
        log.info("存储配置设为默认: configKey={}", saved.getConfigKey());
        return saved;
    }

    /**
     * 判断是否是脱敏后的key（包含****）
     */
    private boolean isMasked(String key) {
        return key != null && key.contains("****");
    }

    /**
     * 标准化StorageConfig的endpoint和customDomain
     */
    private void normalizeEndpoint(StorageConfig config) {
        if (config.getAccessEndpoint() != null) {
            config.setAccessEndpoint(normalizeDomain(config.getAccessEndpoint()));
        }
        if (config.getCustomDomain() != null) {
            config.setCustomDomain(normalizeDomain(config.getCustomDomain()));
        }
    }

    /**
     * 标准化域名：移除前后导点、替换双点为单店
     */
    private String normalizeDomain(String domain) {
        if (domain == null) {
            return null;
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
        // 替换双点为单点
        result = result.replaceAll("\\.+", ".");
        return result;
    }
}
