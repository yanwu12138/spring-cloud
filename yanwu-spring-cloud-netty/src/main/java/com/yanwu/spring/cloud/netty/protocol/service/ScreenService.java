package com.yanwu.spring.cloud.netty.protocol.service;

import com.yanwu.spring.cloud.common.pojo.SortedList;
import com.yanwu.spring.cloud.common.utils.ByteUtil;
import com.yanwu.spring.cloud.netty.cache.ClientSessionCache;
import com.yanwu.spring.cloud.netty.cache.MessageCache;
import com.yanwu.spring.cloud.netty.enums.DeviceRegexEnum;
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
        messageCache.replyMessage(screen.getDeviceNo(), "test1");
        clientSessionCache.putDevice(screen.getDeviceNo(), ctxId);
        messageCache.addQueue(screen.getDeviceNo(), MessageQueueBO.getInstance("B0000001", ScreenService.class));
        SortedList<MessageQueueBO<String>> queues = new SortedList<>();
        queues.add(MessageQueueBO.getInstance("B0000002", ScreenService.class));
        queues.add(MessageQueueBO.getInstance("B0000001", ScreenService.class));
        messageCache.addQueues(screen.getDeviceNo(), queues);
    }

    @Override
    public <T> String assemble(MessageQueueBO<T> param) {
        return (String) param.getMessage();
    }
}
