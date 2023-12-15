package com.yanwu.spring.cloud.netty.controller;

import com.yanwu.spring.cloud.common.cache.ExpiredCache;
import com.yanwu.spring.cloud.common.core.annotation.LogParam;
import com.yanwu.spring.cloud.common.pojo.*;
import com.yanwu.spring.cloud.common.utils.RedisUtil;
import com.yanwu.spring.cloud.common.utils.ThreadUtil;
import com.yanwu.spring.cloud.netty.cache.MessageCache;
import com.yanwu.spring.cloud.netty.handler.TcpHandler;
import com.yanwu.spring.cloud.netty.handler.UdpHandler;
import com.yanwu.spring.cloud.netty.handler.UnicastHandler;
import com.yanwu.spring.cloud.netty.model.MessageQueueBO;
import com.yanwu.spring.cloud.netty.protocol.service.AlarmLampService;
import com.yanwu.spring.cloud.netty.protocol.service.ScreenService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Random;
import java.util.concurrent.Executor;

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
    private UnicastHandler unicastHandler;
    @Resource
    private MessageCache<String> messageCache;
    @Resource
    private Executor nettyExecutor;
    @Resource
    private RedisUtil redisUtil;

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
    public ResponseEnvelope<Result<String>> udpUpgrade(@RequestBody CommandBO<String> command) {
        return ResponseEnvelope.success(unicastHandler.broadcastFile(command.getData(), System.currentTimeMillis()));
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

    @LogParam
    @GetMapping("/remove")
    public ResponseEnvelope<Void> remove() {
        messageCache.removeExpiredMessage();
        return ResponseEnvelope.success();
    }

    @LogParam
    @GetMapping("/testRedisLock")
    public ResponseEnvelope<Void> testRedisLock() {
        //
        nettyExecutor.execute(() -> redisUtil.executor("10000001", Thread.currentThread().getId(), () -> {
            log.info("redis lock 1-1 {}: start", Thread.currentThread().getId());
            ThreadUtil.sleep(10_000);
            log.info("redis lock 1-1 {}: end", Thread.currentThread().getId());
            return Result.success();
        }));
        nettyExecutor.execute(() -> redisUtil.executor("10000001", Thread.currentThread().getId(), () -> {
            log.info("redis lock 1-2 {}: start", Thread.currentThread().getId());
            ThreadUtil.sleep(5_000);
            log.info("redis lock 1-2 {}: end", Thread.currentThread().getId());
            return Result.success();
        }));

        //
        nettyExecutor.execute(() -> redisUtil.executor("10000002", Thread.currentThread().getId(), () -> {
            log.info("redis lock 2-1 {}: start", Thread.currentThread().getId());
            ThreadUtil.sleep(7_000);
            log.info("redis lock 2-1 {}: end", Thread.currentThread().getId());
            return Result.success();
        }));
        nettyExecutor.execute(() -> redisUtil.executor("10000002", Thread.currentThread().getId(), () -> {
            log.info("redis lock 2-2 {}: start", Thread.currentThread().getId());
            ThreadUtil.sleep(8_000);
            log.info("redis lock 2-2 {}: end", Thread.currentThread().getId());
            return Result.success();
        }));

        ThreadUtil.sleep(20_000);
        return ResponseEnvelope.success();
    }


    // ================================================== test ================================================== //
    private static final Random RANDOM = new Random();
    @Resource
    private ExpiredCache<String, String> stringCache;
    @Resource
    private ExpiredCache<String, Integer> integerCache;


    @LogParam
    @GetMapping("/testExpiredCache")
    public ResponseEnvelope<Void> testExpiredCache() {
        testCache();
        return ResponseEnvelope.success();
    }

    private void testCache() {
        int count = 50;
        do {
            String key = RandomStringUtils.randomAlphabetic(12);
            if (RANDOM.nextBoolean()) {
                testStringCache(key);
            } else {
                testIntegerCache(key);
            }
            count--;
            ThreadUtil.sleep(2000L);
        } while (count > 0);
    }

    private void testStringCache(String key) {
        String val = RandomStringUtils.randomAlphabetic(32);
        stringCache.put(key, ExpiredCallbackNode.getInstance(val, randomTime(), (call) -> {
            log.info("test string cache timeout, callback: {}", call);
            return Boolean.TRUE;
        }));
    }

    private void testIntegerCache(String key) {
        Integer val = Integer.parseInt(RandomStringUtils.randomNumeric(8));
        integerCache.put(key, ExpiredCallbackNode.getInstance(val, randomTime(), (call) -> {
            log.info("test string cache timeout, callback: {}", call);
            return Boolean.TRUE;
        }));
    }

    private long randomTime() {
        int random = RANDOM.nextInt(10);
        return (random + 1) * 1_000L;
    }

}
