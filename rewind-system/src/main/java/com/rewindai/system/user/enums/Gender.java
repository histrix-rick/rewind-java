package com.rewindai.system.user.enums;

import lombok.Getter;

/**
 * 性别枚举
 *
 * @author Rewind.ai Team
 */
@Getter
public enum Gender {

    SECRET(0, "保密"),
    MALE(1, "男"),
    FEMALE(2, "女");

    private final Integer code;
    private final String desc;

    Gender(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static Gender fromCode(Integer code) {
        for (Gender gender : values()) {
            if (gender.getCode().equals(code)) {
                return gender;
            }
        }
        return SECRET;
    }
}
