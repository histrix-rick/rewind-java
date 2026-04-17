package com.rewindai.system.sms.provider;

import com.rewindai.system.sms.entity.SmsProviderConfig;
import com.rewindai.system.sms.enums.SmsProvider;
import com.rewindai.system.sms.enums.SmsTemplateType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 阿里云短信服务提供者
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Component
public class AliyunSmsProvider implements com.rewindai.system.sms.provider.SmsProvider {

    @Override
    public boolean sendVerificationCode(SmsProviderConfig config, String phoneNumber, String code, SmsTemplateType templateType) {
        try {
            String templateCode = getTemplateCode(config, templateType);
            if (templateCode == null) {
                log.warn("阿里云短信模板未配置: templateType={}", templateType);
                return false;
            }

            // TODO: 实际集成阿里云短信SDK
            // 暂时模拟成功，后续需要接入真实SDK
            log.info("阿里云短信发送模拟成功: phone={}, signName={}, templateCode={}, code={}",
                    phoneNumber, config.getSignName(), templateCode, code);

            // 以下代码需要在添加阿里云SDK依赖后启用：
            /*
            Client client = createClient(config);
            SendSmsRequest sendSmsRequest = new SendSmsRequest()
                    .setSignName(config.getSignName())
                    .setTemplateCode(templateCode)
                    .setPhoneNumbers(phoneNumber)
                    .setTemplateParam("{\"code\":\"" + code + "\"}");

            SendSmsResponse response = client.sendSms(sendSmsRequest);

            if ("OK".equals(response.getBody().getCode())) {
                log.info("阿里云短信发送成功: phone={}, bizId={}", phoneNumber, response.getBody().getBizId());
                return true;
            } else {
                log.warn("阿里云短信发送失败: phone={}, code={}, message={}",
                        phoneNumber, response.getBody().getCode(), response.getBody().getMessage());
                return false;
            }
            */

            return true;
        } catch (Exception e) {
            log.error("阿里云短信发送异常: phone={}", phoneNumber, e);
            return false;
        }
    }

    @Override
    public String getProviderCode() {
        return SmsProvider.ALIYUN.getCode();
    }

    /*
    private Client createClient(SmsProviderConfig config) throws Exception {
        Config aliyunConfig = new Config()
                .setAccessKeyId(config.getAccessKeyId())
                .setAccessKeySecret(config.getAccessKeySecret());

        if (config.getEndpoint() != null && !config.getEndpoint().isEmpty()) {
            aliyunConfig.setEndpoint(config.getEndpoint());
        } else {
            aliyunConfig.setEndpoint("dysmsapi.aliyuncs.com");
        }

        return new Client(aliyunConfig);
    }
    */

    private String getTemplateCode(SmsProviderConfig config, SmsTemplateType templateType) {
        return switch (templateType) {
            case LOGIN -> config.getTemplateCodeLogin();
            case REGISTER -> config.getTemplateCodeRegister();
            case VERIFY -> config.getTemplateCodeVerify();
        };
    }
}
