package com.rewindai.system.sms.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 短信运营商枚举
 *
 * @author Rewind.ai Team
 */
@Getter
@RequiredArgsConstructor
public enum SmsProvider {
    ALIYUN("ALIYUN", "阿里云短信服务"),
    TENCENT("TENCENT", "腾讯云短信服务"),
    YUNPIAN("YUNPIAN", "云片网短信服务");

    private final String code;
    private final String name;

    public static SmsProvider fromCode(String code) {
        for (SmsProvider provider : values()) {
            if (provider.getCode().equals(code)) {
                return provider;
            }
        }
        throw new IllegalArgumentException("未知的短信运营商: " + code);
    }
}
