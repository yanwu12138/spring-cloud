package com.yanwu.spring.cloud.common.amqp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
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
    public void send(Message message) {
        log.info("SimpleSender send, message: {}", message);
        template.send(message);
    }

    @Override
    public void send(String routingKey, Message message) {
        log.info("SimpleSender send, routingKey: {},  message: {}", routingKey, message);
        template.send(routingKey, message);
    }

    @Override
    public void send(String exchange, String routingKey, Message message) {
        log.info("SimpleSender send, exchange: {}, routingKey: {}, message: {}", exchange, routingKey, message);
        template.send(exchange, routingKey, message);
    }

    @Override
    public void convertAndSend(Object message) {
        log.info("SimpleSender convertAndSend, message: {}", message);
        template.convertAndSend(message);
    }

    @Override
    public void convertAndSend(String routingKey, Object message) {
        log.info("SimpleSender convertAndSend, routingKey: {}, message: {}", routingKey, message);
        template.convertAndSend(routingKey, message);
    }

    @Override
    public void convertAndSend(String exchange, String routingKey, Object message) {
        log.info("SimpleSender convertAndSend, exchange: {}, routingKey: {}, message: {}", exchange, routingKey, message);
        template.convertAndSend(exchange, routingKey, message);
    }

    @Override
    public void convertAndSend(Object message, MessagePostProcessor messagePostProcessor) {
        log.info("SimpleSender convertAndSend, message: {}", message);
        template.convertAndSend(message, messagePostProcessor);
    }

    @Override
    public void convertAndSend(String routingKey, Object message, MessagePostProcessor messagePostProcessor) {
        log.info("SimpleSender convertAndSend, routingKey: {}, message: {}", routingKey, message);
        template.convertAndSend(routingKey, message, messagePostProcessor);
    }

    @Override
    public void convertAndSend(String exchange, String routingKey, Object message, MessagePostProcessor messagePostProcessor) {
        log.info("SimpleSender convertAndSend, exchange: {}, routingKey: {}, message: {}", exchange, routingKey, message);
        template.convertAndSend(exchange, routingKey, message, messagePostProcessor);
    }

    @Override
    public Message sendAndReceive(Message message) {
        Message result = template.sendAndReceive(message);
        log.info("SimpleSender sendAndReceive, message: {}, result: {}", message, result);
        return result;
    }

    @Override
    public Message sendAndReceive(String routingKey, Message message) {
        Message result = template.sendAndReceive(routingKey, message);
        log.info("SimpleSender sendAndReceive, routingKey: {}, message: {}, result: {}", routingKey, message, result);
        return result;
    }

    @Override
    public Message sendAndReceive(String exchange, String routingKey, Message message) {
        Message result = template.sendAndReceive(exchange, routingKey, message);
        log.info("SimpleSender sendAndReceive, exchange: {}, routingKey: {}, message: {}, result: {}", exchange, routingKey, message, result);
        return result;
    }

    @Override
    public Object convertSendAndReceive(Object message) {
        Object result = template.convertSendAndReceive(message);
        log.info("SimpleSender convertSendAndReceive, message: {}, result: {}", message, result);
        return result;
    }

    @Override
    public Object convertSendAndReceive(String routingKey, Object message) {
        Object result = template.convertSendAndReceive(routingKey, message);
        log.info("SimpleSender convertSendAndReceive, routingKey: {},  message: {}, result: {}", routingKey, message, result);
        return result;
    }

    @Override
    public Object convertSendAndReceive(String exchange, String routingKey, Object message) {
        Object result = template.convertSendAndReceive(exchange, routingKey, message);
        log.info("SimpleSender convertSendAndReceive, exchange: {}, routingKey: {}, message: {}, result: {}", exchange, routingKey, message, result);
        return result;
    }

    @Override
    public Object convertSendAndReceive(Object message, MessagePostProcessor messagePostProcessor) {
        Object result = template.convertSendAndReceive(message, messagePostProcessor);
        log.info("SimpleSender convertSendAndReceive, message: {}, result: {}", message, result);
        return result;
    }

    @Override
    public Object convertSendAndReceive(String routingKey, Object message, MessagePostProcessor messagePostProcessor) {
        Object result = template.convertSendAndReceive(routingKey, message, messagePostProcessor);
        log.info("SimpleSender convertSendAndReceive, routingKey: {}, message: {}, result: {}", routingKey, message, result);
        return result;
    }

    @Override
    public Object convertSendAndReceive(String exchange, String routingKey, Object message, MessagePostProcessor messagePostProcessor) {
        Object result = template.convertSendAndReceive(exchange, routingKey, message, messagePostProcessor);
        log.info("SimpleSender convertSendAndReceive, exchange: {}, routingKey: {}, message: {}, result: {}", exchange, routingKey, message, result);
        return result;
    }
}
