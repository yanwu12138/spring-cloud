package com.yanwu.spring.cloud.common.amqp;

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
    void send(T message);

}
