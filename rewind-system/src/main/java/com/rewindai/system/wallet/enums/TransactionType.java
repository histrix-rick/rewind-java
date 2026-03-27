package com.rewindai.system.wallet.enums;

import lombok.Getter;

/**
 * 梦想币交易类型枚举
 *
 * @author Rewind.ai Team
 */
@Getter
public enum TransactionType {

    REWARD(1, "奖励"),
    SHARE(2, "分享收益"),
    CONSUME(3, "消费"),
    TRANSFER_IN(4, "转入"),
    TRANSFER_OUT(5, "转出"),
    ADMIN_GRANT(6, "管理员发放"),
    ADMIN_DEDUCT(7, "管理员扣除");

    private final Integer code;
    private final String desc;

    TransactionType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static TransactionType fromCode(Integer code) {
        for (TransactionType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return REWARD;
    }
}
