package com.rewindai.system.ticket.enums;

import lombok.Getter;

/**
 * 反馈状态枚举
 *
 * @author Rewind.ai Team
 */
@Getter
public enum FeedbackStatus {

    SUBMITTED("SUBMITTED", "已提交"),
    REVIEWING("REVIEWING", "审核中"),
    ACCEPTED("ACCEPTED", "已采纳"),
    REJECTED("REJECTED", "已驳回"),
    IMPLEMENTED("IMPLEMENTED", "已实现");

    private final String code;
    private final String desc;

    FeedbackStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static FeedbackStatus fromCode(String code) {
        for (FeedbackStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return SUBMITTED;
    }
}
