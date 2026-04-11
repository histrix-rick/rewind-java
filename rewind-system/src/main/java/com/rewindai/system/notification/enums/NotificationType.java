package com.rewindai.system.notification.enums;

import lombok.Getter;

/**
 * 通知类型枚举
 *
 * @author Rewind.ai Team
 */
@Getter
public enum NotificationType {

    SYSTEM("SYSTEM", "系统通知"),
    DREAM_LIKE("DREAM_LIKE", "梦境点赞"),
    NODE_LIKE("NODE_LIKE", "节点点赞"),
    DREAM_COMMENT("DREAM_COMMENT", "梦境评论"),
    COMMENT_REPLY("COMMENT_REPLY", "评论回复"),
    DREAM_REWARD("DREAM_REWARD", "梦境打赏"),
    USER_FOLLOW("USER_FOLLOW", "用户关注"),
    DREAM_FOLLOW("DREAM_FOLLOW", "梦境关注");

    private final String code;
    private final String desc;

    NotificationType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static NotificationType fromCode(String code) {
        for (NotificationType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return SYSTEM;
    }
}
