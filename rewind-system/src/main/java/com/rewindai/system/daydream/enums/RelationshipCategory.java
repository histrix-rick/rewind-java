package com.rewindai.system.daydream.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 关系类型分类枚举
 *
 * @author Rewind.ai Team
 */
@Getter
@AllArgsConstructor
public enum RelationshipCategory {

    FAMILY(1, "家人"),
    LOVER(2, "爱人"),
    FRIEND(3, "朋友"),
    COLLEAGUE(4, "同事"),
    TEACHER(5, "老师"),
    OTHER(99, "其他");

    private final Integer code;
    private final String desc;

    public static RelationshipCategory fromCode(Integer code) {
        if (code == null) {
            return OTHER;
        }
        for (RelationshipCategory category : values()) {
            if (category.getCode().equals(code)) {
                return category;
            }
        }
        return OTHER;
    }
}
