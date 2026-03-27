package com.rewindai.system.daydream.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 时间轴节点类型枚举
 *
 * @author Rewind.ai Team
 */
@Getter
public enum NodeType {

    NORMAL(0, "普通节点"),
    CHALLENGE(1, "挑战节点"),
    BRANCH(2, "分支节点");

    @JsonValue
    private final Integer code;
    private final String desc;

    NodeType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static NodeType fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (NodeType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return NORMAL;
    }
}
