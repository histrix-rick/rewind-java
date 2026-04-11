package com.rewindai.system.daydream.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 梦境状态枚举
 *
 * @author Rewind.ai Team
 */
@Getter
public enum DreamStatus {

    DRAFT(0, "草稿"),
    ACTIVE(1, "进行中"),
    COMPLETED(2, "已完成"),
    ARCHIVED(3, "已归档"),
    DELETED(4, "已删除");

    @JsonValue
    private final Integer code;
    private final String desc;

    DreamStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static DreamStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (DreamStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return DRAFT;
    }
}
