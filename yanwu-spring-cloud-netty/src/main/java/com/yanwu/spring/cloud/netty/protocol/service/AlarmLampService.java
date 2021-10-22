package com.yanwu.spring.cloud.netty.protocol.service;

import com.yanwu.spring.cloud.common.pojo.SortedList;
import com.yanwu.spring.cloud.common.utils.ByteUtil;
import com.yanwu.spring.cloud.netty.cache.ClientSessionCache;
import com.yanwu.spring.cloud.netty.cache.MessageCache;
import com.yanwu.spring.cloud.netty.enums.DeviceRegexEnum;
import com.yanwu.spring.cloud.netty.model.MessageQueueBO;
import com.yanwu.spring.cloud.netty.model.alarmLamp.AlarmLampBaseBO;
import com.yanwu.spring.cloud.netty.protocol.AbstractHandler;
import com.yanwu.spring.cloud.netty.util.ResolverUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-11-21 13:02.
 * <p>
 * description:
 */
@Slf4j
@Component("alarmLamp")
public class AlarmLampService extends AbstractHandler {
    @Resource
    private MessageCache messageCache;
    @Resource
    private ClientSessionCache clientSessionCache;

    @Override
    public void analysis(String ctxId, byte[] bytes) throws Exception {
        AlarmLampBaseBO alarmLamp = (AlarmLampBaseBO) ResolverUtil.regexParse(ByteUtil.bytesToHexStr(bytes), DeviceRegexEnum.ALARM_LAMP_REGEX);
        log.info("alarm lamp: {}", alarmLamp);
        messageCache.replyMessage(alarmLamp.getSn(), "test2");
        clientSessionCache.putDevice(alarmLamp.getSn(), ctxId);
        messageCache.addQueue(alarmLamp.getSn(), MessageQueueBO.getInstance("A0000001", "alarmLamp"));
        SortedList<MessageQueueBO> queues = new SortedList<>();
        queues.add(MessageQueueBO.getInstance("A0000002", "alarmLamp"));
        queues.add(MessageQueueBO.getInstance("A0000001", "alarmLamp"));
        messageCache.addQueue(alarmLamp.getSn(), queues);
    }

    @Override
    public String assemble(MessageQueueBO param) {
        return param.getMessage();
    }

}
