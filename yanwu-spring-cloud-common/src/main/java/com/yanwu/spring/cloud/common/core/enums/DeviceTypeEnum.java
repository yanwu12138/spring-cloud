package com.yanwu.spring.cloud.common.core.enums;

import lombok.Getter;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-11-20 17:18.
 * <p>
 * description:
 */
public enum DeviceTypeEnum {
    /*** 告警灯 */
    ALARM_LAMP(0, "alarmLamp", new byte[]{0x48, 0x4C}, new byte[]{0x4C, 0x48}),
    ;

    @Getter
    private Integer code;
    @Getter
    private String type;
    @Getter
    private byte[] head;
    @Getter
    private byte[] end;

    DeviceTypeEnum(Integer code, String type, byte[] head, byte[] end) {
        this.code = code;
        this.type = type;
        this.head = head;
        this.end = end;
    }

    public static DeviceTypeEnum getByType(String type) {
        DeviceTypeEnum[] values = DeviceTypeEnum.values();
        for (DeviceTypeEnum value : values) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

    public static DeviceTypeEnum getByCode(Integer code) {
        DeviceTypeEnum[] values = DeviceTypeEnum.values();
        for (DeviceTypeEnum value : values) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }

}
