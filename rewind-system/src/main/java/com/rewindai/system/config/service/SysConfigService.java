package com.rewindai.system.config.service;

import com.rewindai.common.core.exception.BusinessException;
import com.rewindai.common.core.result.ErrorCode;
import com.rewindai.system.config.entity.SysConfig;
import com.rewindai.system.config.enums.ConfigCategory;
import com.rewindai.system.config.enums.ConfigKey;
import com.rewindai.system.config.repository.SysConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 系统配置 Service
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysConfigService {

    private final SysConfigRepository sysConfigRepository;

    public Optional<SysConfig> findById(Long id) {
        return sysConfigRepository.findById(id);
    }

    public Optional<SysConfig> findByKey(String key) {
        return sysConfigRepository.findByConfigKey(key);
    }

    public String getStringValue(String key) {
        return findByKey(key)
                .map(SysConfig::getConfigValue)
                .orElseGet(() -> getDefaultValue(key));
    }

    public String getStringValue(String key, String defaultValue) {
        return findByKey(key)
                .map(SysConfig::getConfigValue)
                .orElse(defaultValue);
    }

    public Integer getIntValue(String key) {
        String value = getStringValue(key);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            log.warn("配置值不是有效的整数: key={}, value={}", key, value);
            return Integer.parseInt(getDefaultValue(key));
        }
    }

    public Long getLongValue(String key) {
        String value = getStringValue(key);
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            log.warn("配置值不是有效的长整数: key={}, value={}", key, value);
            return Long.parseLong(getDefaultValue(key));
        }
    }

    public Boolean getBooleanValue(String key) {
        String value = getStringValue(key);
        return Boolean.parseBoolean(value);
    }

    public BigDecimal getDecimalValue(String key) {
        String value = getStringValue(key);
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            log.warn("配置值不是有效的小数: key={}, value={}", key, value);
            return new BigDecimal(getDefaultValue(key));
        }
    }

    public List<SysConfig> findByCategory(ConfigCategory category) {
        return sysConfigRepository.findByConfigCategory(category);
    }

    public List<SysConfig> findAll() {
        return sysConfigRepository.findAll();
    }

    public Map<String, SysConfig> findAllAsMap() {
        return sysConfigRepository.findAll().stream()
                .collect(Collectors.toMap(SysConfig::getConfigKey, c -> c));
    }

    @Transactional
    public SysConfig createConfig(SysConfig config) {
        if (sysConfigRepository.findByConfigKey(config.getConfigKey()).isPresent()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "配置key已存在");
        }
        SysConfig saved = sysConfigRepository.save(config);
        log.info("系统配置创建成功: configKey={}", saved.getConfigKey());
        return saved;
    }

    @Transactional
    public SysConfig updateConfig(Long id, String value) {
        SysConfig config = findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "配置不存在"));
        config.setConfigValue(value);
        SysConfig saved = sysConfigRepository.save(config);
        log.info("系统配置更新成功: configKey={}, value={}", saved.getConfigKey(), value);
        return saved;
    }

    @Transactional
    public SysConfig updateConfigByKey(String key, String value) {
        SysConfig config = findByKey(key)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "配置不存在: " + key));
        config.setConfigValue(value);
        SysConfig saved = sysConfigRepository.save(config);
        log.info("系统配置更新成功: configKey={}, value={}", key, value);
        return saved;
    }

    @Transactional
    public void batchUpdateConfigs(Map<String, String> configs) {
        for (Map.Entry<String, String> entry : configs.entrySet()) {
            updateConfigByKey(entry.getKey(), entry.getValue());
        }
    }

    @Transactional
    public void deleteConfig(Long id) {
        SysConfig config = findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "配置不存在"));
        sysConfigRepository.delete(config);
        log.info("系统配置删除成功: configKey={}", config.getConfigKey());
    }

    @Transactional
    public void initializeDefaultConfigs() {
        for (ConfigKey configKey : ConfigKey.values()) {
            if (findByKey(configKey.getKey()).isEmpty()) {
                SysConfig config = SysConfig.builder()
                        .configKey(configKey.getKey())
                        .configName(configKey.getName())
                        .configValue(configKey.getDefaultValue())
                        .configCategory(configKey.getCategory())
                        .valueType(configKey.getValueType())
                        .sortOrder(configKey.ordinal())
                        .build();
                sysConfigRepository.save(config);
                log.info("初始化默认配置: configKey={}", configKey.getKey());
            }
        }
    }

    private String getDefaultValue(String key) {
        for (ConfigKey configKey : ConfigKey.values()) {
            if (configKey.getKey().equals(key)) {
                return configKey.getDefaultValue();
            }
        }
        throw new BusinessException(ErrorCode.BAD_REQUEST, "未知的配置key: " + key);
    }
}
