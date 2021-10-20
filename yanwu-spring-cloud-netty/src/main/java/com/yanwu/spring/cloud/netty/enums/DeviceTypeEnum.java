package com.yanwu.spring.cloud.netty.enums;

import lombok.Getter;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-11-20 17:18.
 * <p>
 * description: 设备对应的帧标识
 */
public enum DeviceTypeEnum {
    /*** 显示屏 ***/
    SCREEN("screen", new byte[]{(byte) 0xAA}, new byte[]{(byte) 0x0B}),
    /*** 告警灯 */
    ALARM_LAMP("alarmLamp", new byte[]{0x48, 0x4C}, new byte[]{0x4C, 0x48}),
    ;

    @Getter
    private final String type;
    @Getter
    private final byte[] head;
    @Getter
    private final byte[] end;

    DeviceTypeEnum(String type, byte[] head, byte[] end) {
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

}
