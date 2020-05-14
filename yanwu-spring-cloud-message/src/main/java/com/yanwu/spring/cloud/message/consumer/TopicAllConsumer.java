package com.yanwu.spring.cloud.message.consumer;

import com.yanwu.spring.cloud.message.bo.MessageBO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.Serializable;

import static com.yanwu.spring.cloud.common.core.common.Contents.Message.TOPIC_QUEUE_ALL_QUEUE;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/14 10:49.
 * <p>
 * description:
 */
@Slf4j
@Component
@RabbitListener(queues = TOPIC_QUEUE_ALL_QUEUE)
public class TopicAllConsumer<T extends Serializable> {

    @RabbitHandler
    public void reader(MessageBO<T> message) {
        log.info("topic reader, queue: {}, param: {}", TOPIC_QUEUE_ALL_QUEUE, message);
    }

}
