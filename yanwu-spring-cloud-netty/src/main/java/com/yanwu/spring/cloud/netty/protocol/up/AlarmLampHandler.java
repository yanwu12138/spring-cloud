package com.yanwu.spring.cloud.netty.protocol.up;

import com.yanwu.spring.cloud.common.utils.JsonUtil;
import com.yanwu.spring.cloud.netty.util.AlarmLampUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-11-20 19:53.
 * <p>
 * description:
 */
@Slf4j
@Component("alarmLamp")
public class AlarmLampHandler extends AbstractHandler {

    @Override
    public void analysis(byte[] bytes) {
        byte[][] message = AlarmLampUtil.parsingMessage(bytes);
        log.info("head: {}", message[0]);
        log.info("data: {}", new String(message[1]));
        log.info("end: {}", message[2]);
    }

}
