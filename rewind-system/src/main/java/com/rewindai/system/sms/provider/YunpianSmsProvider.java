package com.rewindai.system.sms.provider;

import com.rewindai.system.sms.entity.SmsProviderConfig;
import com.rewindai.system.sms.enums.SmsProvider;
import com.rewindai.system.sms.enums.SmsTemplateType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 云片网短信服务提供者
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Component
public class YunpianSmsProvider implements com.rewindai.system.sms.provider.SmsProvider {

    @Override
    public boolean sendVerificationCode(SmsProviderConfig config, String phoneNumber, String code, SmsTemplateType templateType) {
        try {
            log.info("云片网短信发送（待实现）: phone={}, code={}, templateType={}", phoneNumber, code, templateType);
            // TODO: 实现云片网短信发送逻辑
            return false;
        } catch (Exception e) {
            log.error("云片网短信发送异常: phone={}", phoneNumber, e);
            return false;
        }
    }

    @Override
    public String getProviderCode() {
        return SmsProvider.YUNPIAN.getCode();
    }
}
