package com.rewindai.system.sms.service;

import com.rewindai.common.core.exception.BusinessException;
import com.rewindai.common.core.result.ErrorCode;
import com.rewindai.system.sms.entity.SmsProviderConfig;
import com.rewindai.system.sms.enums.SmsProvider;
import com.rewindai.system.sms.repository.SmsProviderConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 短信运营商配置 Service
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SmsProviderConfigService {

    private final SmsProviderConfigRepository smsProviderConfigRepository;

    public Optional<SmsProviderConfig> findById(Long id) {
        return smsProviderConfigRepository.findById(id);
    }

    public Optional<SmsProviderConfig> findByProviderCode(SmsProvider providerCode) {
        return smsProviderConfigRepository.findByProviderCode(providerCode);
    }

    public Optional<SmsProviderConfig> findDefaultProvider() {
        return smsProviderConfigRepository.findByIsDefaultTrue();
    }

    public List<SmsProviderConfig> findActiveProviders() {
        return smsProviderConfigRepository.findByIsActiveTrue();
    }

    public List<SmsProviderConfig> findAll() {
        return smsProviderConfigRepository.findAllByOrderByProviderCodeAsc();
    }

    @Transactional
    public SmsProviderConfig createConfig(SmsProviderConfig config) {
        if (smsProviderConfigRepository.findByProviderCode(config.getProviderCode()).isPresent()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "该运营商配置已存在");
        }

        // 如果设置为默认，先取消其他配置的默认状态
        if (Boolean.TRUE.equals(config.getIsDefault())) {
            clearDefaultFlag();
        }

        SmsProviderConfig saved = smsProviderConfigRepository.save(config);
        log.info("短信运营商配置创建成功: providerCode={}", saved.getProviderCode());
        return saved;
    }

    @Transactional
    public SmsProviderConfig updateConfig(Long id, SmsProviderConfig config) {
        SmsProviderConfig existing = findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "配置不存在"));

        // 如果设置为默认，先取消其他配置的默认状态
        if (Boolean.TRUE.equals(config.getIsDefault()) && !Boolean.TRUE.equals(existing.getIsDefault())) {
            clearDefaultFlag();
        }

        existing.setProviderName(config.getProviderName());
        existing.setAccessKeyId(config.getAccessKeyId());
        existing.setAccessKeySecret(config.getAccessKeySecret());
        existing.setSignName(config.getSignName());
        existing.setTemplateCodeLogin(config.getTemplateCodeLogin());
        existing.setTemplateCodeRegister(config.getTemplateCodeRegister());
        existing.setTemplateCodeVerify(config.getTemplateCodeVerify());
        existing.setEndpoint(config.getEndpoint());
        existing.setRegion(config.getRegion());
        existing.setIsDefault(config.getIsDefault());
        existing.setIsActive(config.getIsActive());
        existing.setCreatedByAdminId(config.getCreatedByAdminId());

        SmsProviderConfig saved = smsProviderConfigRepository.save(existing);
        log.info("短信运营商配置更新成功: providerCode={}", saved.getProviderCode());
        return saved;
    }

    @Transactional
    public void deleteConfig(Long id) {
        SmsProviderConfig config = findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "配置不存在"));

        if (Boolean.TRUE.equals(config.getIsDefault())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "默认配置不能删除");
        }

        smsProviderConfigRepository.delete(config);
        log.info("短信运营商配置删除成功: providerCode={}", config.getProviderCode());
    }

    @Transactional
    public SmsProviderConfig setDefaultProvider(Long id) {
        SmsProviderConfig config = findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "配置不存在"));

        if (!Boolean.TRUE.equals(config.getIsActive())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "只能设置激活的配置为默认");
        }

        clearDefaultFlag();
        config.setIsDefault(true);
        SmsProviderConfig saved = smsProviderConfigRepository.save(config);
        log.info("设置默认短信运营商成功: providerCode={}", saved.getProviderCode());
        return saved;
    }

    private void clearDefaultFlag() {
        List<SmsProviderConfig> allConfigs = findAll();
        for (SmsProviderConfig cfg : allConfigs) {
            if (Boolean.TRUE.equals(cfg.getIsDefault())) {
                cfg.setIsDefault(false);
                smsProviderConfigRepository.save(cfg);
            }
        }
    }
}
