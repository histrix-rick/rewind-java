package com.rewindai.system.sms.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 短信模板类型枚举
 *
 * @author Rewind.ai Team
 */
@Getter
@RequiredArgsConstructor
public enum SmsTemplateType {
    LOGIN("LOGIN", "登录验证码"),
    REGISTER("REGISTER", "注册验证码"),
    VERIFY("VERIFY", "实名认证验证码");

    private final String code;
    private final String name;
}
