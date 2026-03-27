package com.rewindai.system.daydream.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 现金交易类型枚举
 *
 * @author Rewind.ai Team
 */
@Getter
@AllArgsConstructor
public enum CashTransactionType {

    SPEND("SPEND", "支出"),
    EARN("EARN", "收入"),
    INVEST("INVEST", "投资（买入资产）"),
    DIVEST("DIVEST", "撤资（卖出资产）");

    private final String code;
    private final String name;

    public static CashTransactionType fromCode(String code) {
        for (CashTransactionType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return SPEND;
    }
}
