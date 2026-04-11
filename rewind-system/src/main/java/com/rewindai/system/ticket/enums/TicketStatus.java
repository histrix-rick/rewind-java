package com.rewindai.system.ticket.enums;

import lombok.Getter;

/**
 * 工单状态枚举
 *
 * @author Rewind.ai Team
 */
@Getter
public enum TicketStatus {

    PENDING("PENDING", "待处理"),
    PROCESSING("PROCESSING", "处理中"),
    RESOLVED("RESOLVED", "已解决"),
    CLOSED("CLOSED", "已关闭");

    private final String code;
    private final String desc;

    TicketStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static TicketStatus fromCode(String code) {
        for (TicketStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return PENDING;
    }
}
