package com.rewindai.system.daydream.enums;

/**
 * AI判定状态枚举
 *
 * @author Rewind.ai Team
 */
public enum JudgmentStatus {
    PENDING("PENDING", "待处理"),
    PROCESSING("PROCESSING", "处理中"),
    SUCCESS("SUCCESS", "成功"),
    FAILED("FAILED", "失败");

    private final String code;
    private final String description;

    JudgmentStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static JudgmentStatus fromCode(String code) {
        for (JudgmentStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return PENDING;
    }
}
