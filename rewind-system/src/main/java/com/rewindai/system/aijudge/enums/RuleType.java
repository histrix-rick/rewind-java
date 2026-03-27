package com.rewindai.system.aijudge.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * AI 判定规则类型枚举
 *
 * @author Rewind.ai Team
 */
@Getter
public enum RuleType {

    HISTORICAL_FACT(0, "历史事实"),
    PERSONAL_FATE(1, "个人命运"),
    NATURAL_EVENT(2, "自然事件");

    @JsonValue
    private final Integer code;
    private final String desc;

    RuleType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static RuleType fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (RuleType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return PERSONAL_FATE;
    }
}
