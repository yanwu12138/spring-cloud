package com.yanwu.spring.cloud.netty.protocol.service;

import com.yanwu.spring.cloud.common.utils.ByteUtil;
import com.yanwu.spring.cloud.netty.cache.ClientSessionCache;
import com.yanwu.spring.cloud.netty.cache.MessageCache;
import com.yanwu.spring.cloud.netty.enums.DeviceRegexEnum;
import com.yanwu.spring.cloud.netty.model.DeviceBaseBO;
import com.yanwu.spring.cloud.netty.model.MessageQueueBO;
import com.yanwu.spring.cloud.netty.model.screen.ScreenBaseBO;
import com.yanwu.spring.cloud.netty.protocol.AbstractHandler;
import com.yanwu.spring.cloud.netty.util.ResolverUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Baofeng Xu
 * @date 2021/10/20 15:30.
 * <p>
 * description:
 */
@Slf4j
@Component("screen")
public class ScreenService extends AbstractHandler {
    @Resource
    private MessageCache messageCache;
    @Resource
    private ClientSessionCache clientSessionCache;

    @Override
    public void analysis(String ctxId, byte[] bytes) throws Exception {
        ScreenBaseBO screen = (ScreenBaseBO) ResolverUtil.regexParse(ByteUtil.bytesToHexStr(bytes), DeviceRegexEnum.SCREEN_REGEX);
        log.info("screen: {}", screen);
        clientSessionCache.putDevice(screen.getDeviceNo(), ctxId);
        messageCache.addQueue(screen.getDeviceNo(), MessageQueueBO.getInstance("B0000001", "00001"));
        messageCache.addQueue(screen.getDeviceNo(), MessageQueueBO.getInstance("B0000002", "00002"));
        messageCache.addQueue(screen.getDeviceNo(), MessageQueueBO.getInstance("B0000001", "00001"));
    }

    @Override
    public <T extends DeviceBaseBO> String assemble(T param) throws Exception {
        ScreenBaseBO screen = (ScreenBaseBO) param;
        return screen.getHead() + screen.getDeviceNo() + screen.getCode() +
                screen.getData() + screen.getEnd() + screen.getCrc();
    }
}
