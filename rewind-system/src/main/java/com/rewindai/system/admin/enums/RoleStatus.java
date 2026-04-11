package com.rewindai.system.admin.enums;

import lombok.Getter;

/**
 * 角色状态枚举
 *
 * @author Rewind.ai Team
 */
@Getter
public enum RoleStatus {

    ACTIVE("ACTIVE", "启用"),
    INACTIVE("INACTIVE", "禁用");

    private final String code;
    private final String desc;

    RoleStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static RoleStatus fromCode(String code) {
        for (RoleStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return ACTIVE;
    }
}
