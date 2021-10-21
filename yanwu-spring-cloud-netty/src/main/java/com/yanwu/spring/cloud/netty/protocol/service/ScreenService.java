package com.yanwu.spring.cloud.netty.protocol.service;

import com.yanwu.spring.cloud.common.utils.ByteUtil;
import com.yanwu.spring.cloud.netty.enums.DeviceRegexEnum;
import com.yanwu.spring.cloud.netty.model.DeviceBaseBO;
import com.yanwu.spring.cloud.netty.model.screen.ScreenBaseBO;
import com.yanwu.spring.cloud.netty.protocol.AbstractHandler;
import com.yanwu.spring.cloud.netty.util.ResolverUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Baofeng Xu
 * @date 2021/10/20 15:30.
 * <p>
 * description:
 */
@Slf4j
@Component("screen")
public class ScreenService extends AbstractHandler {

    @Override
    public void analysis(String ctxId, byte[] bytes) throws Exception {
        ScreenBaseBO screen = (ScreenBaseBO) ResolverUtil.regexParse(ByteUtil.bytesToHexStr(bytes), DeviceRegexEnum.SCREEN_REGEX);
        log.info("screen: {}", screen);
        sendTcpMessage(ctxId, screen);
    }

    @Override
    public <T extends DeviceBaseBO> String assemble(T param) throws Exception {
        ScreenBaseBO screen = (ScreenBaseBO) param;
        return screen.getHead() + screen.getDeviceNo() + screen.getCode() +
                screen.getData() + screen.getEnd() + screen.getCrc();
    }
}
