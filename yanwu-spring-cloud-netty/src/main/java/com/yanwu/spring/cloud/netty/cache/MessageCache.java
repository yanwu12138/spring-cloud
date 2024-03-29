package com.yanwu.spring.cloud.netty.cache;

import com.yanwu.spring.cloud.common.pojo.Result;
import com.yanwu.spring.cloud.common.pojo.SortedList;
import com.yanwu.spring.cloud.common.utils.RedisUtil;
import com.yanwu.spring.cloud.netty.handler.TcpHandler;
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
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Baofeng Xu
 * @date 2021/10/20 16:44.
 * <p>
 * description:
 * 使用redis的zset做消息队列，按照入队的顺序对消息进行排序。
 * 当消息的key重复时，则删除旧的消息，保存新的消息，并将新消息排在队尾。
 * 每个消息默认执行3次重发，当重发3次仍然未回复时，则认为消息发送失败不再重试，等设备再次上线时再尝试发送。
 * <p>
 * 该消息缓存处理分为3个模块：
 * 1. zset：消息队列【key：设备的唯一标识；value：消息的唯一标识；score：消息的入队时间】
 * <p>
 * 2. hash：消息的发送状态【key：统一的key；field：设备的唯一标志；value：消息的发送状态】
 * <p>
 * 3. string：消息内容【key：消息的唯一标识；value：消息详细内容】
 */
@Slf4j
@Component
public class MessageCache<T> {

    private static final String DEVICE_QUEUE = "device:queue:";
    private static final String DEVICE_MESSAGE = "device:message:";
    private static final String DEVICE_STATUS = "device:message:status";

    /*** 一个月的数据为过期数据 ***/
    private static final Long EXPIRED_TIME = 30 * 24 * 60 * 60 * 1000L;

    @Resource
    private RedisUtil redisUtil;
    @Resource
    private TcpHandler tcpHandler;

    /**
     * 消息队列，根据消息的入队时间进行分数的计算
     * key：设备的唯一标识 - device:queue:{sn}
     * value：消息的唯一标识 - MessageQueueBO#key
     * score：消息的入队时间
     */
    @SuppressWarnings("all")
    @Resource(name = "redisTemplate")
    private ZSetOperations<String, String> queuesOperations;

    /**
     * 使用redis的zSet为小站的消息进行排序，然后根据顺序下发消息
     * key：消息的唯一标识 - MessageQueueBO#key
     * value：消息详细内容
     */
    @SuppressWarnings("all")
    @Resource(name = "redisTemplate")
    private ValueOperations<String, MessageQueueBO<T>> messagesOperations;

    /**
     * 设备消息的发送状态
     * key：统一的key
     * field：设备的唯一标志
     * value：消息的发送状态
     */
    @SuppressWarnings("all")
    @Resource(name = "redisTemplate")
    private HashOperations<String, String, MessageStatusBO<T>> statusOperations;

    /**
     * 批量添加消息到缓存
     *
     * @param sn       设备唯一标志
     * @param messages 消息集合
     */
    public Result<Void> addQueues(String sn, SortedList<MessageQueueBO<T>> messages) {
        if (StringUtils.isBlank(sn) || CollectionUtils.isEmpty(messages)) {
            return Result.failed("SN或queues为空");
        }
        return redisUtil.executor(sn, () -> {
            String queueKey = getQueueKey(sn);
            log.info("add queues, queueKey: {}, messages: {}", queueKey, messages);
            // ----- 根据设备标识拿到当前设备的最高分数
            Set<ZSetOperations.TypedTuple<String>> range = getAllQueue(queueKey);
            double maxScore = getMaxScore(range);
            // ----- 消息处理：将同key的旧的消息删除，增加新的消息入队
            Map<String, MessageQueueBO<T>> messageMap = new HashMap<>(messages.size());
            for (MessageQueueBO<T> message : messages) {
                messageMap.put(message.getKey(), message);
                range.removeIf(msg -> Objects.equals(msg.getValue(), message.getKey()));
                range.add(new DefaultTypedTuple<>(message.getKey(), ++maxScore));
            }
            // ----- 将消息加入队列（zset && value）
            return redisUtil.multiExec(() -> {
                queuesOperations.removeRange(queueKey, 0, -1);
                if (range.size() > 0) {
                    queuesOperations.add(queueKey, range);
                }
                messagesOperations.multiSet(messageMap);
                return Result.success();
            });
        });
    }

    /**
     * 添加消息到缓存
     *
     * @param sn      设备唯一标志
     * @param message 消息
     */
    public Result<Void> addQueue(String sn, MessageQueueBO<T> message) {
        if (StringUtils.isBlank(sn) || message == null) {
            return Result.failed("SN或queue为空");
        }
        return redisUtil.executor(sn, () -> {
            String queueKey = getQueueKey(sn);
            log.info("add queue, queueKey: {}, message: {}", queueKey, message);
            // ----- 根据设备标识拿到当前设备的最高分数
            Set<ZSetOperations.TypedTuple<String>> range = getAllQueue(queueKey);
            double maxScore = getMaxScore(range);
            // ----- 消息处理：将同key的旧的消息删除，增加新的消息入队
            range.removeIf(msg -> Objects.equals(msg.getValue(), message.getKey()));
            range.add(new DefaultTypedTuple<>(message.getKey(), ++maxScore));
            // ----- 将消息加入队列（zset && value）
            return redisUtil.multiExec(() -> {
                queuesOperations.removeRange(queueKey, 0, -1);
                if (range.size() > 0) {
                    queuesOperations.add(queueKey, range);
                }
                messagesOperations.set(message.getKey(), message);
                return Result.success();
            });
        });
    }

    /**
     * 删除过期消息
     *
     * @return 被删除的消息
     */
    public Result<Map<String, MessageQueueBO<T>>> removeExpiredMessage() {
        Set<String> sns = statusOperations.keys(DEVICE_STATUS);
        if (CollectionUtils.isEmpty(sns)) {
            return Result.success();
        }
        Map<String, MessageQueueBO<T>> result = new HashMap<>();
        sns.forEach(sn -> {
            // ----- 判断消息是否过期
            redisUtil.executor(sn, () -> {
                MessageStatusBO<T> status = statusOperations.get(DEVICE_STATUS, sn);
                if (status == null) {
                    return Result.success();
                }
                if ((System.currentTimeMillis() - status.getLastSendTime()) > EXPIRED_TIME) {
                    String queueKey = getQueueKey(sn);
                    // ----- 该消息过期，删除该消息缓存，并将消息写入文件
                    statusOperations.delete(DEVICE_STATUS, sn);
                    Set<ZSetOperations.TypedTuple<String>> range = getAllQueue(queueKey);
                    if (CollectionUtils.isNotEmpty(range)) {
                        range.forEach(queue -> {
                            if (queue == null || StringUtils.isBlank(queue.getValue())) {
                                return;
                            }
                            MessageQueueBO<T> message = messagesOperations.get(queue.getValue());
                            if ((System.currentTimeMillis() - status.getLastSendTime()) > EXPIRED_TIME) {
                                // ----- 删除过期消息
                                result.put(queue.getValue(), message);
                                messagesOperations.getOperations().delete(queue.getValue());
                            }
                        });
                        // ----- 刷新queue：删除过期消息；保留未过期消息
                        range.removeIf(queue -> result.containsKey(queue.getValue()));
                        queuesOperations.removeRange(queueKey, 0, -1);
                        if (range.size() > 0) {
                            queuesOperations.add(queueKey, range);
                        }
                    }
                }
                return Result.success();
            });
        });
        return Result.success(result);
    }

    /**
     * 定时器 - 定时发送消息
     *
     * @param sn 设备唯一标志
     */
    public void senderMessage(String sn) {
        redisUtil.executor(sn, () -> {
            String queueKey = getQueueKey(sn);
            MessageStatusBO<T> status = statusOperations.get(DEVICE_STATUS, sn);
            if (status == null || status.getMessage() == null) {
                // ----- 当前没有发送中状态的数据，取出队列中的第一条数据直接发送
                Set<String> queues = queuesOperations.range(queueKey, 0, 0);
                if (CollectionUtils.isEmpty(queues)) {
                    return Result.success();
                }
                String queue = queues.stream().findFirst().orElse(null);
                if (StringUtils.isNotBlank(queue)) {
                    MessageQueueBO<T> message = messagesOperations.get(queue);
                    long messageId = status == null ? 0L : status.getMessageId();
                    status = MessageStatusBO.nextMessage(sn, message, messageId);
                    senderMessage(sn, status);
                    queuesOperations.remove(queueKey, status.getMessage().getKey());
                    return Result.success();
                }
            }
            // ----- 当前已有发送中状态的数据，判断是否可以进行下一次发送
            if (status != null && status.getMessage() != null && status.canSend()) {
                senderMessage(sn, status);
            }
            return Result.success();
        });
    }

    private void senderMessage(String sn, MessageStatusBO<T> status) throws Exception {
        tcpHandler.send(sn, status.getMessage());
        status = status.successSend();
        statusOperations.put(DEVICE_STATUS, sn, status);
    }

    /**
     * 收到消息回复后，删除发送中消息状态，等待发送下一条消息
     *
     * @param sn        设备唯一标识
     * @param messageId 消息ID
     */
    public void replyMessage(String sn, Long messageId) {
        redisUtil.executor(sn, () -> {
            // ----- 删除已经发发送成功的消息
            MessageStatusBO<T> status = statusOperations.get(DEVICE_STATUS, sn);
            if (status == null || status.getMessage() == null || status.getMessageId().compareTo(messageId) != 0) {
                return Result.success();
            }
            messagesOperations.getOperations().delete(status.getMessage().getKey());
            // ----- 看还有没有待发送的消息：如果有则取出一条新消息发送
            String queueKey = getQueueKey(sn);
            Set<String> queues = queuesOperations.range(queueKey, 0, 0);
            if (CollectionUtils.isEmpty(queues)) {
                // ----- 没有待发送的消息
                status = status.clearMessage();
                statusOperations.put(DEVICE_STATUS, sn, status);
                return Result.success();
            }
            // ----- 有待发送的消息：取出一条新消息进行发送
            nextSenderMessage(sn, queues.stream().findFirst().orElse(null), queueKey, status);
            return Result.success();
        });
    }

    /**
     * @param sn       设备唯一标识
     * @param queue    当前准备发送的消息KEY
     * @param queueKey 设备的队列KEY
     * @param status   设备发送消息状态
     * @throws Exception .
     */
    private void nextSenderMessage(String sn, String queue, String queueKey, MessageStatusBO<T> status) throws Exception {
        if (StringUtils.isNotBlank(queue)) {
            MessageQueueBO<T> message = messagesOperations.get(queue);
            while (message == null) {
                queuesOperations.remove(queueKey, queue);
                Set<String> queues = queuesOperations.range(queueKey, 0, 0);
                if (CollectionUtils.isEmpty(queues)) {
                    return;
                }
                queue = queues.stream().findFirst().orElse(null);
                if (StringUtils.isNotBlank(queue)) {
                    message = messagesOperations.get(queue);
                }
            }
            status = MessageStatusBO.nextMessage(sn, message, status.getMessageId());
            senderMessage(sn, status);
            queuesOperations.remove(queueKey, queue);
        }
    }

    /**
     * 设备上线时，重置消息发送次数
     *
     * @param sn 设备唯一标志
     */
    public void online(String sn) {
        redisUtil.executor(sn, () -> {
            MessageStatusBO<T> status = statusOperations.get(DEVICE_STATUS, sn);
            if (status != null) {
                status.setTryNumber(3);
                statusOperations.put(DEVICE_STATUS, sn, status);
            }
            return Result.success();
        });
    }

    /**
     * 获取队列中最大的分数
     *
     * @param range 队列
     * @return 最大的分数
     */
    private double getMaxScore(Set<ZSetOperations.TypedTuple<String>> range) {
        if (CollectionUtils.isEmpty(range)) {
            return 0D;
        }
        AtomicReference<Double> maxScore = new AtomicReference<>(0D);
        range.forEach(queue -> maxScore.set(Math.max(maxScore.get(), (queue.getScore() == null ? 0D : queue.getScore()))));
        return maxScore.get();
    }

    /**
     * 获取该小站的所有待发送消息
     *
     * @param queueKey 小站的KEY
     * @return 待发送消息队列
     */
    private Set<ZSetOperations.TypedTuple<String>> getAllQueue(String queueKey) {
        Set<ZSetOperations.TypedTuple<String>> range = queuesOperations.rangeWithScores(queueKey, 0, -1);
        return CollectionUtils.isEmpty(range) ? Collections.emptySet() : range;
    }

    private String getQueueKey(String sn) {
        return DEVICE_QUEUE + sn;
    }

    public String getMessageKey(String... strs) {
        Assert.isTrue((strs != null && strs.length > 0), "get message key error, because strs is empty.");
        return DEVICE_MESSAGE + String.join(":", strs);
    }
}
