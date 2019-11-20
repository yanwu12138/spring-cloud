package com.yanwu.spring.cloud.netty.util;

import com.yanwu.spring.cloud.common.core.enums.DeviceTypeEnum;
import lombok.Data;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-11-20 17:14.
 * <p>
 * description:
 */
@Data
public class DeviceUtil {

    public static DeviceTypeEnum getDeviceType(byte[] bytes) {
        return DeviceTypeEnum.getByBytes(bytes);
    }

}
