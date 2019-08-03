package com.yanwu.spring.cloud.common.amqp;

import com.yanwu.spring.cloud.common.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
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
public class RabbitMQSender {

    @Autowired
    private AmqpTemplate amqpTemplate;

    public void convertAndSend(String queueName, Object message) {
        log.info("rabbitMQ  : send message, [queueName]: {}, [message]: {}", queueName, message);
        amqpTemplate.convertAndSend(queueName, JsonUtil.toJsonString(message));
    }

    public void convertAndSend(String exchange, String routingKey, Object message) {
        log.info("rabbitMQ  : send message, [exchange]: {}, [routingKey]: {}, [message]: {}", exchange, routingKey, message);
        amqpTemplate.convertAndSend(exchange, routingKey, JsonUtil.toJsonString(message));
    }

    public void convertAndSend(String queueName, Object message, MessagePostProcessor messagePostProcessor) {
        log.info("rabbitMQ  : send message, [queueName]: {}, [message]: {}", queueName, message);
        amqpTemplate.convertAndSend(queueName, message, messagePostProcessor);
    }

    public void convertAndSend(String exchange, String routingKey, Object message, MessagePostProcessor messagePostProcessor) {
        log.info("rabbitMQ  : send message, [exchange]: {}, [routingKey]: {}, [message]: {}", exchange, routingKey, message);
        amqpTemplate.convertAndSend(exchange, routingKey, JsonUtil.toJsonString(message), messagePostProcessor);
    }

    public Object convertSendAndReceive(String queueName, Object message) {
        Object result = amqpTemplate.convertSendAndReceive(queueName, JsonUtil.toJsonString(message));
        log.info("rabbitMQ  : send message, [queueName]: {},  [message]: {}, [result]: {}", queueName, message, result);
        return result;
    }

    public Object convertSendAndReceive(String exchange, String routingKey, Object message) {
        Object result = amqpTemplate.convertSendAndReceive(exchange, routingKey, JsonUtil.toJsonString(message));
        log.info("rabbitMQ  : send message, [exchange]: {}, [routingKey]: {}, [message]: {}, [result]: {}", exchange, routingKey, message, result);
        return result;
    }

    public Object convertSendAndReceive(String queueName, Object message, MessagePostProcessor messagePostProcessor) {
        Object result = amqpTemplate.convertSendAndReceive(queueName, message, messagePostProcessor);
        log.info("rabbitMQ  : send message, [queueName]: {}, [message]: {}, [result]: {}", queueName, message, result);
        return result;
    }

    public Object convertSendAndReceive(String exchange, String routingKey, Object message, MessagePostProcessor messagePostProcessor) {
        Object result = amqpTemplate.convertSendAndReceive(exchange, routingKey, JsonUtil.toJsonString(message), messagePostProcessor);
        log.info("rabbitMQ  : send message, exchange: {}, routingKey: {}, message: {}, result: {}", exchange, routingKey, message, result);
        return result;
    }

}
