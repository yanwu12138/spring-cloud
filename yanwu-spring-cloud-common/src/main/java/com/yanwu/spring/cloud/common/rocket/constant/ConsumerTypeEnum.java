package com.yanwu.spring.cloud.common.rocket.constant;

import lombok.Getter;

/**
 * @author Baofeng Xu
 * @date 2021/11/22 10:51.
 * <p>
 * description: 消费的实现类型
 */
public enum ConsumerTypeEnum {

    /*** 顺序消息 ***/
    ORDERLY(0),
    /*** 普通消息 ***/
    CONCURRENTLY(1),
    ;

    @Getter
    private final Integer status;

    ConsumerTypeEnum(Integer status) {
        this.status = status;
    }

    public static ConsumerTypeEnum getByStatus(Integer status) {
        for (ConsumerTypeEnum value : ConsumerTypeEnum.values()) {
            if (value.status.equals(status)) {
                return value;
            }
        }
        return null;
    }
}
