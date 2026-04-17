package com.rewindai.system.sms.provider;

import com.rewindai.system.sms.entity.SmsProviderConfig;
import com.rewindai.system.sms.enums.SmsTemplateType;

/**
 * 短信服务提供者接口
 *
 * @author Rewind.ai Team
 */
public interface SmsProvider {

    /**
     * 发送验证码短信
     *
     * @param config 短信运营商配置
     * @param phoneNumber 手机号
     * @param code 验证码
     * @param templateType 模板类型
     * @return 是否发送成功
     */
    boolean sendVerificationCode(SmsProviderConfig config, String phoneNumber, String code, SmsTemplateType templateType);

    /**
     * 获取供应商编码
     *
     * @return 供应商编码
     */
    String getProviderCode();
}
