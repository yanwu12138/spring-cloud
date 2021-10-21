package com.yanwu.spring.cloud.netty.cache;

import com.yanwu.spring.cloud.common.pojo.CallableResult;
import com.yanwu.spring.cloud.common.utils.RedisUtil;
import com.yanwu.spring.cloud.netty.model.MessageQueueBO;
import com.yanwu.spring.cloud.netty.model.MessageStatusBO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author Baofeng Xu
 * @date 2021/10/20 16:44.
 * <p>
 * description:
 */
@Slf4j
@Component
@SuppressWarnings("all")
public class MessageCache {

    private static final String DEVICE_SEQ_QUEUE = "device:queue:";
    private static final String DEVICE_QUEUE = "device:message:status";
    private static final String DEVICE_MESSAGE = "device:message";

    @Resource
    private RedisUtil redisUtil;

    /**
     * 使用redis的zSet为小站的消息进行排序，然后根据顺序下发消息
     * KEY: queue:device:{sn}
     * VALUE: messages.key 的有序集合
     */
    @Resource(name = "redisTemplate")
    private ZSetOperations<String, String> queuesOperations;

    /**
     * 根据设备的唯一标志、消息的type、去重规则进行消息的去重，将去重后的消息按顺序下发给小站。
     * 该缓存的key缓存在queue的value中
     * KEY: message:device:{sn}:{type}:{去重规则}
     * VALUE: 需要下发给edge的消息
     */
    @Resource(name = "redisTemplate")
    private ValueOperations<String, String> messagesOperations;

    /**
     * 设备消息的发送状态
     * HASH: 设备的唯一标志
     * VALUE: 设备消息的发送状态
     */
    @Resource(name = "redisTemplate")
    private HashOperations<String, String, MessageStatusBO> statusOperations;

    /**
     * 批量添加消息到缓存
     *
     * @param sn     设备唯一标志
     * @param queues 消息集合
     */
    public CallableResult<Void> addQueue(String sn, List<MessageQueueBO> queues) {
        if (StringUtils.isBlank(sn) || CollectionUtils.isEmpty(queues)) {
            return CallableResult.failed("SN或queues为空");
        }
        return redisUtil.executor(sn, Thread.currentThread().getId(), () -> {
            // ----- 获取该设备的消息最大分数
            String queueKey = DEVICE_SEQ_QUEUE + sn;
            Set<ZSetOperations.TypedTuple<String>> queueSet = queuesOperations.rangeWithScores(queueKey, 0, -1);
            double maxScore = 0D;
            if (CollectionUtils.isNotEmpty(queueSet)) {
                maxScore = queueSet.stream().mapToDouble(ZSetOperations.TypedTuple::getScore).max().orElse(0D);
            }

            // ----- 处理消息
            Map<String, String> messages = new HashMap<>();
            for (MessageQueueBO queue : queues) {
                log.info("add queue: {}", queue);
                String messageKey = String.join(":", DEVICE_MESSAGE, sn, queue.getCode());
                // ----- 消息的key && value
                messages.put(messageKey, queue.getMessage());
                // ----- 对消息进行去重，然后将消息重新计算分数加入queueSet
                queueSet.removeIf(a -> Objects.equals(a.getValue(), messageKey));
                queueSet.add(new DefaultTypedTuple<>(messageKey, ++maxScore));
            }

            // ----- 放入缓存
            return redisUtil.multiExec(() -> {
                queuesOperations.removeRange(queueKey, 0, -1);
                if (queueSet.size() > 0) {
                    queuesOperations.add(queueKey, queueSet);
                }
                messagesOperations.multiSet(messages);
                return CallableResult.success();
            });
        });
    }

    /**
     * 添加消息到缓存
     *
     * @param sn      设备唯一标志
     * @param message 消息
     */
    public CallableResult<Void> addQueue(String sn, MessageQueueBO queue) {
        if (StringUtils.isBlank(sn) || queue == null) {
            return CallableResult.failed("SN或queue为空");
        }
        return redisUtil.executor(sn, Thread.currentThread().getId(), () -> {
            log.info("add queue: {}", queue);
            String queueKey = DEVICE_SEQ_QUEUE + sn;
            Set<ZSetOperations.TypedTuple<String>> queueSet = queuesOperations.rangeWithScores(queueKey, 0, -1);
            // ----- 获取该设备的消息最大分数
            double maxScore = 0D;
            if (CollectionUtils.isNotEmpty(queueSet)) {
                maxScore = queueSet.stream().mapToDouble(ZSetOperations.TypedTuple::getScore).max().orElse(0D);
            }
            // ----- 处理消息
            String messageKey = String.join(":", DEVICE_MESSAGE, sn, queue.getCode());
            queueSet.removeIf(a -> Objects.equals(a.getValue(), messageKey));
            queueSet.add(new DefaultTypedTuple<>(messageKey, ++maxScore));
            // ----- 放入缓存
            return redisUtil.multiExec(() -> {
                queuesOperations.removeRange(queueKey, 0, -1);
                if (queueSet.size() > 0) {
                    queuesOperations.add(queueKey, queueSet);
                }
                messagesOperations.set(messageKey, queue.getMessage());
                return CallableResult.success();
            });
        });
    }

    public MessageStatusBO getStatus(String sn) {
        return statusOperations.get(DEVICE_QUEUE, sn);
    }

}
