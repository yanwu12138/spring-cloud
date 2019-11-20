package com.yanwu.spring.cloud.netty.handler;

import com.yanwu.spring.cloud.common.core.enums.DeviceTypeEnum;
import com.yanwu.spring.cloud.netty.protocol.up.AbstractHandler;
import com.yanwu.spring.cloud.netty.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-11-20 19:41.
 * <p>
 * description:
 */
@Slf4j
@Component
public class DeviceHandlerFactory {

    static AbstractHandler newInstance(DeviceTypeEnum deviceType) {
        if (deviceType == null) {
            return null;
        }
        String type = deviceType.getType();
        return (AbstractHandler) SpringUtil.getBean(type);
    }

}
