package com.yanwu.spring.cloud.common.amqp;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-08-02 15:14.
 * <p>
 * description:
 */
public interface Sender<T> {

    /**
     * 发送消息
     *
     * @param message
     */
    void send(Message message);

    void send(String routingKey, Message message);

    void send(String exchange, String routingKey, Message message);

    void convertAndSend(Object message);

    void convertAndSend(String routingKey, Object message);

    void convertAndSend(String exchange, String routingKey, Object message);

    void convertAndSend(Object message, MessagePostProcessor messagePostProcessor);

    void convertAndSend(String routingKey, Object message, MessagePostProcessor messagePostProcessor);

    void convertAndSend(String exchange, String routingKey, Object message, MessagePostProcessor messagePostProcessor);

    Message sendAndReceive(Message message);

    Message sendAndReceive(String routingKey, Message message);

    Message sendAndReceive(String exchange, String routingKey, Message message);

    Object convertSendAndReceive(Object message);

    Object convertSendAndReceive(String routingKey, Object message);

    Object convertSendAndReceive(String exchange, String routingKey, Object message);

    Object convertSendAndReceive(Object message, MessagePostProcessor messagePostProcessor);

    Object convertSendAndReceive(String routingKey, Object message, MessagePostProcessor messagePostProcessor);

    Object convertSendAndReceive(String exchange, String routingKey, Object message, MessagePostProcessor messagePostProcessor);

}
