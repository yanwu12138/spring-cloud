package com.yanwu.spring.cloud.common.amqp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.ReceiveAndReplyCallback;
import org.springframework.amqp.core.ReplyToAddressCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2019/08/03
 * <p>
 * describe:
 */
@Slf4j
@Component
public class SimpleReader implements Reader {

    @Autowired
    private AmqpTemplate template;

    @Override
    public Message receive() {
        Message result = template.receive();
        log.info("SimpleReader receive, result: {}", result);
        return result;
    }

    @Override
    public Message receive(String queueName) {
        Message result = template.receive(queueName);
        log.info("SimpleReader receive, queueName: {} result: {}", queueName, result);
        return result;
    }

    @Override
    public Message receive(long timeoutMillis) {
        Message result = template.receive(timeoutMillis);
        log.info("SimpleReader receive, timeoutMillis: {} result: {}", timeoutMillis, result);
        return result;
    }

    @Override
    public Message receive(String queueName, long timeoutMillis) {
        Message result = template.receive(queueName, timeoutMillis);
        log.info("SimpleReader receive, queueName: {}, timeoutMillis: {}, result: {}", queueName, timeoutMillis, result);
        return result;
    }

    @Override
    public Object receiveAndConvert() {
        Object result = template.receiveAndConvert();
        log.info("SimpleReader receiveAndConvert, result: {}", result);
        return result;
    }

    @Override
    public Object receiveAndConvert(String queueName) {
        Object result = template.receiveAndConvert(queueName);
        log.info("SimpleReader receiveAndConvert, queueName: {}, result: {}", queueName, result);
        return result;
    }

    @Override
    public Object receiveAndConvert(long timeoutMillis) {
        Object result = template.receiveAndConvert(timeoutMillis);
        log.info("SimpleReader receiveAndConvert, timeoutMillis: {}, result: {}", timeoutMillis, result);
        return result;
    }

    @Override
    public Object receiveAndConvert(String queueName, long timeoutMillis) {
        Object result = template.receiveAndConvert(queueName, timeoutMillis);
        log.info("SimpleReader receiveAndConvert, queueName: {}, timeoutMillis: {}, result: {}", queueName, timeoutMillis, result);
        return result;
    }

    @Override
    public <R, S> boolean receiveAndReply(ReceiveAndReplyCallback<R, S> callback) {
        return template.receiveAndReply(callback);
    }

    @Override
    public <R, S> boolean receiveAndReply(String queueName, ReceiveAndReplyCallback<R, S> callback) {
        return template.receiveAndReply(queueName, callback);
    }

    @Override
    public <R, S> boolean receiveAndReply(ReceiveAndReplyCallback<R, S> callback, String replyExchange, String replyRoutingKey) {
        return template.receiveAndReply(callback, replyExchange, replyRoutingKey);
    }

    @Override
    public <R, S> boolean receiveAndReply(String queueName, ReceiveAndReplyCallback<R, S> callback, String replyExchange, String replyRoutingKey) {
        return template.receiveAndReply(queueName, callback, replyExchange, replyRoutingKey);
    }

    @Override
    public <R, S> boolean receiveAndReply(ReceiveAndReplyCallback<R, S> callback, ReplyToAddressCallback<S> replyToAddressCallback) {
        return template.receiveAndReply(callback, replyToAddressCallback);
    }

    @Override
    public <R, S> boolean receiveAndReply(String queueName, ReceiveAndReplyCallback<R, S> callback, ReplyToAddressCallback<S> replyToAddressCallback) {
        return template.receiveAndReply(queueName, callback, replyToAddressCallback);
    }

}
