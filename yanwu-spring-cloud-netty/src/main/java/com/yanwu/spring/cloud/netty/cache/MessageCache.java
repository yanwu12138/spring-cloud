package com.yanwu.spring.cloud.netty.cache;

import com.yanwu.spring.cloud.common.pojo.CallableResult;
import com.yanwu.spring.cloud.common.utils.RedisUtil;
import com.yanwu.spring.cloud.netty.handler.TcpHandler;
import com.yanwu.spring.cloud.netty.model.MessageQueueBO;
import com.yanwu.spring.cloud.netty.model.MessageStatusBO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

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
    @Resource
    private TcpHandler tcpHandler;

    /**
     * 使用redis的zSet为小站的消息进行排序，然后根据顺序下发消息
     * TODO: *** 该队列保证左近右出 ***
     * KEY: queue:device:{sn}
     * VALUE: messages
     */
    @Resource(name = "redisTemplate")
    private ListOperations<String, MessageQueueBO> queuesOperations;

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
    public CallableResult<Long> addQueue(String sn, List<MessageQueueBO> queues) {
        if (StringUtils.isBlank(sn) || CollectionUtils.isEmpty(queues)) {
            return CallableResult.failed("SN或queues为空");
        }
        return redisUtil.executor(sn, Thread.currentThread().getId(), () -> {
            String queueKey = DEVICE_SEQ_QUEUE + sn;
            Long result = queuesOperations.leftPushAll(queueKey, queues);
            log.info("add queues, queueKey: {}, queues: {}, result: {}", queueKey, queues, result);
            return CallableResult.success(result);
        });
    }

    /**
     * 添加消息到缓存
     *
     * @param sn      设备唯一标志
     * @param message 消息
     */
    public CallableResult<Long> addQueue(String sn, MessageQueueBO queue) {
        if (StringUtils.isBlank(sn) || queue == null) {
            return CallableResult.failed("SN或queue为空");
        }
        return redisUtil.executor(sn, Thread.currentThread().getId(), () -> {
            String queueKey = DEVICE_SEQ_QUEUE + sn;
            Long result = queuesOperations.leftPush(queueKey, queue);
            log.info("add queue, queueKey: {}, queue: {}, result: {}", queueKey, queue, result);
            return CallableResult.success(result);
        });
    }

    /**
     * 定时器 - 定时发送消息
     *
     * @param sn 设备唯一标志
     */
    public void senderMessage(String sn) {
        redisUtil.executor(sn, Thread.currentThread().getId(), () -> {
            MessageStatusBO status = statusOperations.get(DEVICE_QUEUE, sn);
            if (status == null) {
                // ----- 当前没有发送中状态的数据，取出队列最右的一条数据直接发送
                String queueKey = DEVICE_SEQ_QUEUE + sn;
                MessageQueueBO queue = queuesOperations.rightPop(queueKey);
                if (queue != null) {
                    status = MessageStatusBO.getInstance(sn, queue);
                    tcpHandler.send(sn, status.getMessage());
                    status.successSend();
                    statusOperations.put(DEVICE_QUEUE, sn, status);
                }
            } else {
                // ----- 当前已有发送中状态的数据，判断是否可以进行下一次发送
                if (status.canSend()) {
                    tcpHandler.send(sn, status.getMessage());
                    status.successSend();
                    statusOperations.put(DEVICE_QUEUE, sn, status);
                }
            }
            return CallableResult.success();
        });
    }

    /**
     * 收到消息回复后，删除发送中消息状态，等待发送下一条消息
     *
     * @param sn        设备唯一标识
     * @param messageId 消息ID
     */
    public void replyMessage(String sn, String messageId) {
        redisUtil.executor(sn, Thread.currentThread().getId(), () -> {
            statusOperations.delete(DEVICE_QUEUE, sn);
            return CallableResult.success();
        });
    }

    /**
     * 设备上线时，重置消息发送次数
     *
     * @param sn 设备唯一标志
     */
    public void online(String sn) {
        redisUtil.executor(sn, Thread.currentThread().getId(), () -> {
            MessageStatusBO status = statusOperations.get(DEVICE_QUEUE, sn);
            if (status != null) {
                status.setTryTimes(3);
                statusOperations.put(DEVICE_QUEUE, sn, status);
            }
            return CallableResult.success();
        });
    }
}
