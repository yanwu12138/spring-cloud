package com.yanwu.spring.cloud.common.amqp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-08-02 15:15.
 * <p>
 * description:
 */
@Slf4j
@Component
public class SimpleSender implements Sender {

    @Autowired
    private AmqpTemplate template;

    @Override
    public void send(Object message) {
        template.convertAndSend(message);
    }
}
