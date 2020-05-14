package com.yanwu.spring.cloud.message.consumer;

import com.yanwu.spring.cloud.message.bo.MessageBO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.Serializable;

import static com.yanwu.spring.cloud.common.core.common.Contents.Message.FANOUT_LOTUS_QUEUE_NAME;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/14 10:26.
 * <p>
 * description:
 */
@Slf4j
@Component
@RabbitListener(queues = FANOUT_LOTUS_QUEUE_NAME)
public class FanoutLotusConsumer<T extends Serializable> {

    @RabbitHandler
    public void reader(MessageBO<T> message) {
        log.info("fanout reader, queue: {}, param: {}", FANOUT_LOTUS_QUEUE_NAME, message);
    }

}
