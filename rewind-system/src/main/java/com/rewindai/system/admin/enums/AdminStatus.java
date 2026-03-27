package com.rewindai.system.admin.enums;

import lombok.Getter;

/**
 * 管理员状态枚举
 *
 * @author Rewind.ai Team
 */
@Getter
public enum AdminStatus {

    NORMAL(1, "正常"),
    DISABLED(2, "禁用"),
    PENDING_CHANGE(3, "待首次改密");

    private final Integer code;
    private final String desc;

    AdminStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static AdminStatus fromCode(Integer code) {
        for (AdminStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return NORMAL;
    }
}
