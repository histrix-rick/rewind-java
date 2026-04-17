package com.rewindai.system.sms.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 短信发送状态枚举
 *
 * @author Rewind.ai Team
 */
@Getter
@RequiredArgsConstructor
public enum SmsSendStatus {
    PENDING("PENDING", "待发送"),
    SENT("SENT", "已发送"),
    FAILED("FAILED", "发送失败");

    private final String code;
    private final String name;
}
