package com.yanwu.spring.cloud.message.consumer.rabbit;

import com.rabbitmq.client.Channel;
import com.yanwu.spring.cloud.message.bo.MessageBO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.yanwu.spring.cloud.common.core.common.Contents.Message.TOPIC_QUEUE_LOTUS_QUEUE;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/14 10:50.
 * <p>
 * description:
 */
@Slf4j
@Component
public class TopicLotusConsumer {

    @Resource
    private MyAckConsumer myAckConsumer;

    @RabbitListener(queues = TOPIC_QUEUE_LOTUS_QUEUE)
    public void reader(Message message, Channel channel) throws Exception {
        MessageBO messageBO = MessageBO.newInstance(message.getBody());
        log.info("topic reader, queue: {}, message: {}", TOPIC_QUEUE_LOTUS_QUEUE, messageBO);
        myAckConsumer.basicAck(message, channel);
    }

}
