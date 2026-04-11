package com.rewindai.system.ticket.enums;

import lombok.Getter;

/**
 * 工单分类枚举
 *
 * @author Rewind.ai Team
 */
@Getter
public enum TicketCategory {

    ACCOUNT("ACCOUNT", "账号问题"),
    PAYMENT("PAYMENT", "支付问题"),
    CONTENT("CONTENT", "内容问题"),
    TECHNICAL("TECHNICAL", "技术问题"),
    SUGGESTION("SUGGESTION", "建议反馈"),
    OTHER("OTHER", "其他");

    private final String code;
    private final String desc;

    TicketCategory(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static TicketCategory fromCode(String code) {
        for (TicketCategory category : values()) {
            if (category.getCode().equals(code)) {
                return category;
            }
        }
        return OTHER;
    }
}
