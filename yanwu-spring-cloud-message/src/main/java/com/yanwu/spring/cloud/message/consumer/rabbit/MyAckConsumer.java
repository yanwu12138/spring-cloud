package com.yanwu.spring.cloud.message.consumer.rabbit;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/14 11:36.
 * <p>
 * description:
 */
@Slf4j
@Component
public class MyAckConsumer {

    /**
     * 消息确认
     *
     * @param message 消息
     * @param channel 通道
     * @throws Exception Exception.class
     */
    public void basicAck(Message message, Channel channel) throws Exception {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        String queue = message.getMessageProperties().getConsumerQueue();
        String exchange = message.getMessageProperties().getReceivedExchange();
        String msg = new String(message.getBody(), Charset.defaultCharset());
        try {
            log.info("my ack consumer success, message: {}, exchange: {}, queue: {}, tag: {}", msg, exchange, queue, deliveryTag);
            channel.basicAck(deliveryTag, true);
        } catch (Exception e) {
            log.error("my ack consumer failed, message: {}, exchange: {}, queue: {}, tag: {}", msg, exchange, queue, deliveryTag, e);
            // ----- 为true会重新放回队列
            channel.basicReject(deliveryTag, true);
        }
    }

}
