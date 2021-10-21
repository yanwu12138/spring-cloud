package com.yanwu.spring.cloud.netty.util;

import com.yanwu.spring.cloud.netty.enums.DeviceTypeEnum;
import lombok.extern.slf4j.Slf4j;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-11-20 17:14.
 * <p>
 * description:
 */
@Slf4j
public class DeviceUtil {
    /*** 消息间隔时间：上一次消息发送完30秒后才能发送下一条消息 ***/
    public static final long SEND_INTERVAL = 30 * 1000;

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

    /**
     * 是否可以发送消息：上一次消息发送完30秒后才能发送下一条消息（上一条消息发送时间与当前时间是否间隔超过30秒）
     *
     * @param messageLastSendTime 上一次发送消息的时间
     * @return 【true: 超过30秒; false: 不超过30秒】
     */
    public static boolean canSend(long messageLastSendTime) {
        return System.currentTimeMillis() - messageLastSendTime > SEND_INTERVAL;
    }

}
