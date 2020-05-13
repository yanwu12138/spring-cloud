package com.yanwu.spring.cloud.message.demo;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/13 17:04.
 * <p>
 * description:
 * * 需要导入依赖
 * <dependency>
 * <groupId>com.rabbitmq</groupId>
 * <artifactId>amqp-client</artifactId>
 * <version>3.4.1</version>
 * </dependency>
 */
public class Constant {
    public static final Boolean TRUE = true;
    public static final Boolean FALSE = false;
    public static final Executor EXECUTOR;
    public static final String SIMPLE_QUEUE_NAME = "test_simple_queue";
    public static final String WORK_QUEUE_NAME = "test_work_queue";

    static {
        EXECUTOR = Executors.newSingleThreadExecutor();
    }

    public static void close(Connection connection, Channel channel) throws Exception {
        channel.close();
        connection.close();
    }
}
