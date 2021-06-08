package com.yanwu.spring.cloud.netty.util;

import com.yanwu.spring.cloud.common.core.enums.DeviceTypeEnum;
import lombok.extern.slf4j.Slf4j;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-11-20 17:14.
 * <p>
 * description:
 */
@Slf4j
public class DeviceUtil {

    private DeviceUtil() {
        throw new UnsupportedOperationException("DeviceUtil should never be instantiated");
    }

    /**
     * 根据报文获取设备类型枚举
     *
     * @param bytes 报文
     * @return 设备类型
     */
    public static DeviceTypeEnum getDeviceType(byte[] bytes) {
        DeviceTypeEnum[] values = DeviceTypeEnum.values();
        for (DeviceTypeEnum value : values) {
            // ----- 处理帧头
            byte[] head = value.getHead();
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
            byte[] end = value.getEnd();
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

}
