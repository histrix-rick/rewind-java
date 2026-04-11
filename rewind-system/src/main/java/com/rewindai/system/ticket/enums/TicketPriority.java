package com.rewindai.system.ticket.enums;

import lombok.Getter;

/**
 * 工单优先级枚举
 *
 * @author Rewind.ai Team
 */
@Getter
public enum TicketPriority {

    LOW("LOW", "低"),
    MEDIUM("MEDIUM", "中"),
    HIGH("HIGH", "高"),
    URGENT("URGENT", "紧急");

    private final String code;
    private final String desc;

    TicketPriority(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static TicketPriority fromCode(String code) {
        for (TicketPriority priority : values()) {
            if (priority.getCode().equals(code)) {
                return priority;
            }
        }
        return MEDIUM;
    }
}
