package com.rewindai.system.daydream.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 梦境隐私设置枚举
 *
 * @author Rewind.ai Team
 */
@Getter
public enum DreamPrivacy {

    PRIVATE(0, "私密"),
    FRIENDS_ONLY(1, "仅好友可见"),
    PUBLIC(2, "公开");

    @JsonValue
    private final Integer code;
    private final String desc;

    DreamPrivacy(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static DreamPrivacy fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (DreamPrivacy privacy : values()) {
            if (privacy.getCode().equals(code)) {
                return privacy;
            }
        }
        return PRIVATE;
    }
}
