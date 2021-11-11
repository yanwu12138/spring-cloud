package com.yanwu.spring.cloud.netty.controller;

import com.yanwu.spring.cloud.common.core.annotation.LogParam;
import com.yanwu.spring.cloud.common.pojo.CallableResult;
import com.yanwu.spring.cloud.common.pojo.CommandBO;
import com.yanwu.spring.cloud.common.pojo.ResponseEnvelope;
import com.yanwu.spring.cloud.common.pojo.SortedList;
import com.yanwu.spring.cloud.common.utils.ThreadUtil;
import com.yanwu.spring.cloud.netty.cache.MessageCache;
import com.yanwu.spring.cloud.netty.handler.TcpHandler;
import com.yanwu.spring.cloud.netty.handler.UdpHandler;
import com.yanwu.spring.cloud.netty.handler.UpgradeHandler;
import com.yanwu.spring.cloud.netty.model.MessageQueueBO;
import com.yanwu.spring.cloud.netty.protocol.service.AlarmLampService;
import com.yanwu.spring.cloud.netty.protocol.service.ScreenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-08-09 14:45.
 * <p>
 * description:
 */
@Slf4j
@RestController
@RequestMapping("webapp/netty/")
public class NettyController {

    @Resource
    private TcpHandler tcpHandler;
    @Resource
    private UdpHandler udpHandler;
    @Resource
    private UpgradeHandler upgradeHandler;
    @Resource
    private MessageCache<String> messageCache;

    @LogParam
    @PostMapping("/tcp/send")
    public void tcpSend(@RequestBody CommandBO<String> command) {
        tcpHandler.send(command.getCtxId(), String.valueOf(command.getData()));
    }

    @LogParam
    @PostMapping("/udp/send")
    public void udpSend(@RequestBody CommandBO<String> command) {
        udpHandler.send(command.getCtxId(), String.valueOf(command.getData()));
    }

    @LogParam
    @PostMapping("/udp/radio")
    public void udpRadio(@RequestBody CommandBO<String> command) {
        udpHandler.radio(String.valueOf(command.getData()));
    }

    @LogParam
    @PostMapping("/udp/upgrade")
    public ResponseEnvelope<CallableResult<String>> udpUpgrade(@RequestBody CommandBO<String> command) {
        return ResponseEnvelope.success(upgradeHandler.broadcastFile(command.getData(), System.currentTimeMillis()));
    }

    @LogParam
    @GetMapping("/test")
    public ResponseEnvelope<Void> test() {
        // ----- alarmLamp
        SortedList<MessageQueueBO<String>> alarmQueues = new SortedList<>();
        messageCache.addQueue("131420210123", MessageQueueBO.getInstance(messageCache.getMessageKey("A0000001"), "A0000001", AlarmLampService.class));
        ThreadUtil.sleep(10);
        alarmQueues.add(MessageQueueBO.getInstance(messageCache.getMessageKey("A0000002"), "A0000002", AlarmLampService.class));
        ThreadUtil.sleep(10);
        alarmQueues.add(MessageQueueBO.getInstance(messageCache.getMessageKey("A0000001"), "A0000003", AlarmLampService.class));
        messageCache.addQueues("131420210123", alarmQueues);

        // ----- screen
        SortedList<MessageQueueBO<String>> screenQueues = new SortedList<>();
        messageCache.addQueue("2F30", MessageQueueBO.getInstance(messageCache.getMessageKey("B0000001"), "B0000001", ScreenService.class));
        ThreadUtil.sleep(10);
        screenQueues.add(MessageQueueBO.getInstance(messageCache.getMessageKey("B0000002"), "B0000002", ScreenService.class));
        ThreadUtil.sleep(10);
        screenQueues.add(MessageQueueBO.getInstance(messageCache.getMessageKey("B0000001"), "B0000003", ScreenService.class));
        messageCache.addQueues("2F30", screenQueues);
        return ResponseEnvelope.success();
    }

}
