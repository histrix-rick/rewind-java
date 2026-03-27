package com.rewindai.system.user.enums;

import lombok.Getter;

/**
 * 用户状态枚举
 *
 * @author Rewind.ai Team
 */
@Getter
public enum UserStatus {

    SECRET(0, "保密"),
    NORMAL(1, "正常"),
    MUTED(2, "禁言"),
    BANNED(3, "封号");

    private final Integer code;
    private final String desc;

    UserStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static UserStatus fromCode(Integer code) {
        for (UserStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return SECRET;
    }
}
