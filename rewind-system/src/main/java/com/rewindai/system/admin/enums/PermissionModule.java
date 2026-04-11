package com.rewindai.system.admin.enums;

import lombok.Getter;

/**
 * 权限模块枚举
 *
 * @author Rewind.ai Team
 */
@Getter
public enum PermissionModule {

    USER("USER", "用户管理"),
    DREAM("DREAM", "梦境管理"),
    SOCIAL("SOCIAL", "社交管理"),
    WALLET("WALLET", "钱包管理"),
    COMMENT("COMMENT", "评论管理"),
    CLOUD_STORAGE("CLOUD_STORAGE", "云存储管理"),
    FINANCE("FINANCE", "财务管理"),
    NOTIFICATION("NOTIFICATION", "通知管理"),
    ANALYSIS("ANALYSIS", "数据分析"),
    SYSCONFIG("SYSCONFIG", "系统配置"),
    SECURITY("SECURITY", "安全与风控"),
    ADMIN("ADMIN", "管理员管理"),
    ROLE("ROLE", "角色管理"),
    PERMISSION("PERMISSION", "权限管理");

    private final String code;
    private final String desc;

    PermissionModule(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static PermissionModule fromCode(String code) {
        for (PermissionModule module : values()) {
            if (module.getCode().equals(code)) {
                return module;
            }
        }
        return USER;
    }
}
