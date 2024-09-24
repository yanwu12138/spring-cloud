package com.yanwu.spring.cloud.message.consumer.rabbit;

import com.rabbitmq.client.Channel;
import com.yanwu.spring.cloud.message.bo.MessageBO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.yanwu.spring.cloud.common.core.common.Contents.Message.FANOUT_LOTUS_QUEUE_NAME;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/14 10:26.
 * <p>
 * description:
 */
@Slf4j
@Component
public class FanoutLotusConsumer {

    @Resource
    private MyAckConsumer myAckConsumer;

    @RabbitListener(queues = FANOUT_LOTUS_QUEUE_NAME)
    public void reader(Message message, Channel channel) throws Exception {
        MessageBO messageBO = MessageBO.getInstance(message.getBody());
        log.info("fanout reader, queue: {}, message: {}", FANOUT_LOTUS_QUEUE_NAME, messageBO);
        myAckConsumer.basicAck(message, channel);
    }

}
