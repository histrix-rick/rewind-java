package com.rewindai.system.sms.service;

import com.rewindai.common.core.exception.BusinessException;
import com.rewindai.common.core.result.ErrorCode;
import com.rewindai.system.admin.entity.SysVerificationCode;
import com.rewindai.system.admin.repository.SysVerificationCodeRepository;
import com.rewindai.system.config.enums.ConfigKey;
import com.rewindai.system.config.service.SysConfigService;
import com.rewindai.system.sms.entity.SmsProviderConfig;
import com.rewindai.system.sms.enums.SmsProvider;
import com.rewindai.system.sms.enums.SmsSendStatus;
import com.rewindai.system.sms.enums.SmsTemplateType;
import com.rewindai.system.sms.provider.SmsProviderFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Random;

/**
 * 短信服务
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SmsService {

    private final SysConfigService sysConfigService;
    private final SmsProviderConfigService smsProviderConfigService;
    private final SmsProviderFactory smsProviderFactory;
    private final SysVerificationCodeRepository verificationCodeRepository;

    private final Random random = new Random();

    /**
     * 发送验证码短信
     *
     * @param phoneNumber 手机号
     * @param templateType 模板类型
     * @return 验证码
     */
    @Transactional
    public String sendVerificationCode(String phoneNumber, SmsTemplateType templateType) {
        // 检查发送频率限制
        checkSendLimit(phoneNumber);

        // 生成验证码
        String code = generateCode();

        // 检查是否启用短信服务
        boolean smsEnabled = sysConfigService.getBooleanValue(ConfigKey.SMS_ENABLED.getKey());
        String testCode = sysConfigService.getStringValue(ConfigKey.SMS_TEST_CODE.getKey());

        if (!smsEnabled) {
            // 短信服务关闭，使用测试验证码
            code = testCode;
            log.info("短信服务已关闭，使用测试验证码: phone={}, code={}", phoneNumber, code);
            saveVerificationCode(phoneNumber, code, templateType, null, SmsSendStatus.SENT, "测试模式，未真实发送");
            return code;
        }

        // 获取默认短信提供商
        SmsProviderConfig providerConfig = smsProviderConfigService.findDefaultProvider()
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "未配置默认短信运营商"));

        if (!Boolean.TRUE.equals(providerConfig.getIsActive())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "默认短信运营商未激活");
        }

        // 保存验证码记录（待发送状态）
        SysVerificationCode verificationCode = saveVerificationCode(
                phoneNumber, code, templateType, providerConfig.getProviderCode(),
                SmsSendStatus.PENDING, null);

        // 发送短信
        com.rewindai.system.sms.provider.SmsProvider provider = smsProviderFactory.getProvider(providerConfig);
        boolean sendSuccess = provider.sendVerificationCode(providerConfig, phoneNumber, code, templateType);

        // 更新发送状态
        if (sendSuccess) {
            verificationCode.setSendStatus(SmsSendStatus.SENT);
            verificationCode.setSendResult("发送成功");
        } else {
            verificationCode.setSendStatus(SmsSendStatus.FAILED);
            verificationCode.setSendResult("发送失败");
            throw new BusinessException(ErrorCode.BAD_REQUEST, "短信发送失败，请稍后重试");
        }

        verificationCodeRepository.save(verificationCode);
        log.info("验证码发送成功: phone={}, templateType={}", phoneNumber, templateType);

        return code;
    }

    /**
     * 验证验证码
     *
     * @param phoneNumber 手机号
     * @param templateType 模板类型
     * @param code 验证码
     * @return 是否验证成功
     */
    @Transactional
    public boolean verifyCode(String phoneNumber, SmsTemplateType templateType, String code) {
        // 检查是否启用短信服务，如果关闭则使用测试验证码
        boolean smsEnabled = sysConfigService.getBooleanValue(ConfigKey.SMS_ENABLED.getKey());
        String testCode = sysConfigService.getStringValue(ConfigKey.SMS_TEST_CODE.getKey());

        if (!smsEnabled && testCode.equals(code)) {
            log.info("测试模式，验证码验证通过: phone={}, code={}", phoneNumber, code);
            return true;
        }

        Optional<SysVerificationCode> opt = verificationCodeRepository
                .findFirstByReceiverAndTypeAndIsUsedFalseAndExpireAtAfterOrderByCreatedAtDesc(
                        phoneNumber, templateType.getCode(), OffsetDateTime.now()
                );

        if (opt.isEmpty()) {
            log.warn("验证码不存在或已过期: phone={}", phoneNumber);
            return false;
        }

        SysVerificationCode verificationCode = opt.get();
        if (!verificationCode.getCode().equals(code)) {
            log.warn("验证码错误: phone={}, input={}, expected={}", phoneNumber, code, verificationCode.getCode());
            return false;
        }

        verificationCode.setIsUsed(true);
        verificationCode.setUsedAt(OffsetDateTime.now());
        verificationCodeRepository.save(verificationCode);

        log.info("验证码验证成功: phone={}", phoneNumber);
        return true;
    }

    /**
     * 检查发送频率限制
     */
    private void checkSendLimit(String phoneNumber) {
        int intervalSeconds = sysConfigService.getIntValue(ConfigKey.SMS_SEND_INTERVAL_SECONDS.getKey());
        int dailyLimit = sysConfigService.getIntValue(ConfigKey.SMS_DAILY_LIMIT.getKey());

        // 检查发送间隔
        OffsetDateTime intervalTime = OffsetDateTime.now().minusSeconds(intervalSeconds);
        Optional<SysVerificationCode> recentCode = verificationCodeRepository
                .findFirstByReceiverAndCreatedAtAfterOrderByCreatedAtDesc(phoneNumber, intervalTime);

        if (recentCode.isPresent()) {
            long secondsLeft = intervalSeconds - java.time.Duration.between(recentCode.get().getCreatedAt(), OffsetDateTime.now()).getSeconds();
            throw new BusinessException(ErrorCode.BAD_REQUEST, "发送太频繁，请" + secondsLeft + "秒后重试");
        }

        // 检查今日发送次数
        OffsetDateTime todayStart = OffsetDateTime.now().toLocalDate().atStartOfDay().atOffset(OffsetDateTime.now().getOffset());
        long todayCount = verificationCodeRepository.countByReceiverAndCreatedAtAfter(phoneNumber, todayStart);

        if (todayCount >= dailyLimit) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "今日发送次数已达上限");
        }
    }

    /**
     * 生成验证码
     */
    private String generateCode() {
        int length = sysConfigService.getIntValue(ConfigKey.SMS_CODE_LENGTH.getKey());
        int min = (int) Math.pow(10, length - 1);
        int max = (int) Math.pow(10, length) - 1;
        int code = min + random.nextInt(max - min + 1);
        return String.valueOf(code);
    }

    /**
     * 保存验证码记录
     */
    private SysVerificationCode saveVerificationCode(String phoneNumber, String code,
            SmsTemplateType templateType, SmsProvider providerCode,
            SmsSendStatus sendStatus, String sendResult) {
        int expireMinutes = sysConfigService.getIntValue(ConfigKey.SMS_CODE_EXPIRE_MINUTES.getKey());

        SysVerificationCode verificationCode = SysVerificationCode.builder()
                .receiver(phoneNumber)
                .code(code)
                .type(templateType.getCode())
                .targetType("PHONE")
                .providerCode(providerCode)
                .sendStatus(sendStatus)
                .sendResult(sendResult)
                .isUsed(false)
                .expireAt(OffsetDateTime.now().plusMinutes(expireMinutes))
                .build();

        return verificationCodeRepository.save(verificationCode);
    }
}
