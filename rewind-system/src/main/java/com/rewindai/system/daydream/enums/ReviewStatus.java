package com.rewindai.system.daydream.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 内容审核状态枚举
 *
 * @author Rewind.ai Team
 */
@Getter
public enum ReviewStatus {

    PENDING(0, "待审核"),
    APPROVED(1, "已通过"),
    REJECTED(2, "已驳回");

    @JsonValue
    private final Integer code;
    private final String desc;

    ReviewStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static ReviewStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (ReviewStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return PENDING;
    }
}
