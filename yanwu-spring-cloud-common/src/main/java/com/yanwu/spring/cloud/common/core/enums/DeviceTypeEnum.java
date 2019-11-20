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
    TEST(1, "test", new byte[]{0x48, 0x4C}, new byte[]{0x48, 0x4C});

    @Getter
    private Integer code;
    @Getter
    private String type;
    @Getter
    private byte[] head;
    @Getter
    private byte[] end;

    private DeviceTypeEnum(Integer code, String type, byte[] head, byte[] end) {
        this.code = code;
        this.type = type;
        this.head = head;
        this.end = end;
    }

    public static void main(String[] args) {
        byte[] param = {72, 76, 2, 0, 0, 0, 0, 91, 0, 34, 125, 119, 82, 76, 72};
        DeviceTypeEnum type = getByBytes(param);
        System.out.println(type);
    }

    public static DeviceTypeEnum getByBytes(byte[] bytes) {
        DeviceTypeEnum[] values = DeviceTypeEnum.values();
        for (DeviceTypeEnum value : values) {
            // ----- 处理帧头
            byte[] head = value.head;
            boolean headFlag = false;
            for (int i = 0; i < head.length; i++) {
                if (head[i] != bytes[i]) {
                    headFlag = false;
                    break;
                }
                headFlag = true;
            }
            if (!headFlag) {
                continue;
            }
            // ----- 处理帧尾
            byte[] end = value.end;
            int index = 0;
            for (int i = head.length - 1; i >= 0; i--) {
                index++;
                if (end[i] != bytes[bytes.length - index]) {
                    headFlag = false;
                    break;
                }
                headFlag = true;
            }
            if (headFlag) {
                return value;
            }
        }
        return null;
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
