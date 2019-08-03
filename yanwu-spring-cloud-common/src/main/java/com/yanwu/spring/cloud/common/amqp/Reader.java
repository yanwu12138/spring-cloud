package com.yanwu.spring.cloud.common.amqp;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.ReceiveAndReplyCallback;
import org.springframework.amqp.core.ReplyToAddressCallback;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2019/08/03
 * <p>
 * describe:
 */
public interface Reader {

    Message receive();

    Message receive(String queueName);

    Message receive(long timeoutMillis);

    Message receive(String queueName, long timeoutMillis);

    Object receiveAndConvert();

    Object receiveAndConvert(String queueName);

    Object receiveAndConvert(long timeoutMillis);

    Object receiveAndConvert(String queueName, long timeoutMillis);

    <R, S> boolean receiveAndReply(ReceiveAndReplyCallback<R, S> callback);

    <R, S> boolean receiveAndReply(String queueName, ReceiveAndReplyCallback<R, S> callback);

    <R, S> boolean receiveAndReply(ReceiveAndReplyCallback<R, S> callback, String replyExchange, String replyRoutingKey);

    <R, S> boolean receiveAndReply(String queueName, ReceiveAndReplyCallback<R, S> callback, String replyExchange, String replyRoutingKey);

    <R, S> boolean receiveAndReply(ReceiveAndReplyCallback<R, S> callback, ReplyToAddressCallback<S> replyToAddressCallback);

    <R, S> boolean receiveAndReply(String queueName, ReceiveAndReplyCallback<R, S> callback, ReplyToAddressCallback<S> replyToAddressCallback);


}
