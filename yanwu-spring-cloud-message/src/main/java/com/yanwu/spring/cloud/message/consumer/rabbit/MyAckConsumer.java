package com.yanwu.spring.cloud.message.consumer.rabbit;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/14 11:36.
 * <p>
 * description:
 */
@Slf4j
@Component
public class MyAckConsumer implements ChannelAwareMessageListener {

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        String queueName = message.getMessageProperties().getConsumerQueue();
        String msg = new String(message.getBody());
        try {
            log.info("my ack consumer, message: {}, consumer: {}, tag: {}", msg, queueName, deliveryTag);
            channel.basicAck(deliveryTag, true);
        } catch (Exception e) {
            log.error("my ack consumer error, message: {}, consumer: {}, tag: {}", msg, queueName, deliveryTag, e);
            // ----- 为true会重新放回队列
            channel.basicReject(deliveryTag, true);
        }
    }

}
