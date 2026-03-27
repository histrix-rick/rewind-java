package com.rewindai.system.daydream.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 资产类型枚举
 *
 * @author Rewind.ai Team
 */
@Getter
@AllArgsConstructor
public enum AssetType {

    REALTY("REALTY", "房产", true),
    STOCK("STOCK", "股票", false),
    CASH("CASH", "现金", false),
    VEHICLE("VEHICLE", "车辆", false),
    GOLD("GOLD", "黄金", false),
    BITCOIN("BITCOIN", "比特币", false),
    OTHER("OTHER", "其他", false);

    private final String code;
    private final String name;
    private final boolean needsLocation;

    public static AssetType fromCode(String code) {
        for (AssetType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return OTHER;
    }
}
