package com.rewindai.system.daydream.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 题目科目枚举
 *
 * @author Rewind.ai Team
 */
@Getter
@AllArgsConstructor
public enum QuestionSubject {

    CHINESE("CHINESE", "语文"),
    MATH("MATH", "数学"),
    ENGLISH("ENGLISH", "英语");

    private final String code;
    private final String desc;

    public static QuestionSubject fromCode(String code) {
        if (code == null) {
            return CHINESE;
        }
        for (QuestionSubject subject : values()) {
            if (subject.getCode().equals(code)) {
                return subject;
            }
        }
        return CHINESE;
    }
}
