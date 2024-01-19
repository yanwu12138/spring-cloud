package com.yanwu.spring.cloud.common.core.enums;

import lombok.Getter;

/**
 * @author XuBaofeng.
 * @date 2024/1/17 18:40.
 * <p>
 * description:
 */
@Getter
public enum AccessTypeEnum {

    USER(1),
    ROLE(2),
    ;

    private final int type;

    AccessTypeEnum(int type) {
        this.type = type;
    }

    public static AccessTypeEnum getInstance(int accesses) {
        for (AccessTypeEnum value : AccessTypeEnum.values()) {
            if (value.type == accesses) {
                return value;
            }
        }
        return null;
    }

}
