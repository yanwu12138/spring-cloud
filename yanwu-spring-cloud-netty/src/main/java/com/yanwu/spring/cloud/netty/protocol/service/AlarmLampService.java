package com.yanwu.spring.cloud.netty.protocol.service;

import com.yanwu.spring.cloud.common.utils.ByteUtil;
import com.yanwu.spring.cloud.netty.enums.DeviceRegexEnum;
import com.yanwu.spring.cloud.netty.model.DeviceBaseBO;
import com.yanwu.spring.cloud.netty.model.alarmLamp.AlarmLampBaseBO;
import com.yanwu.spring.cloud.netty.protocol.AbstractHandler;
import com.yanwu.spring.cloud.netty.util.ResolverUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-11-21 13:02.
 * <p>
 * description:
 */
@Slf4j
@Component("alarmLamp")
public class AlarmLampService extends AbstractHandler {

    @Override
    public void analysis(String ctxId, byte[] bytes) throws Exception {
        AlarmLampBaseBO alarmLamp = (AlarmLampBaseBO) ResolverUtil.regexParse(ByteUtil.bytesToHexStr(bytes), DeviceRegexEnum.ALARM_LAMP_REGEX);
        log.info("alarm lamp: {}", alarmLamp);
        sendTcpMessage(ctxId, alarmLamp);
    }

    @Override
    public <T extends DeviceBaseBO> String assemble(T param) throws Exception {
        AlarmLampBaseBO alarmLamp = (AlarmLampBaseBO) param;
        return alarmLamp.getHead() + alarmLamp.getSn() + alarmLamp.getSeq() + alarmLamp.getMcode() +
                alarmLamp.getCcode() + alarmLamp.getData() + alarmLamp.getEnd() + alarmLamp.getCrc();
    }

}
