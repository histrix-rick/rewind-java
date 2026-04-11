package com.rewindai.system.admin.enums;

import lombok.Getter;

/**
 * 权限类型枚举
 *
 * @author Rewind.ai Team
 */
@Getter
public enum PermissionType {

    MENU("MENU", "菜单"),
    BUTTON("BUTTON", "按钮"),
    API("API", "接口");

    private final String code;
    private final String desc;

    PermissionType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static PermissionType fromCode(String code) {
        for (PermissionType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return BUTTON;
    }
}
