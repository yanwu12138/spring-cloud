package com.yanwu.spring.cloud.message.demo;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

import static com.yanwu.spring.cloud.message.demo.Constant.*;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/13 16:56.
 * <p>
 * description: 发送消息
 * * 需要导入依赖
 * <dependency>
 * <groupId>com.rabbitmq</groupId>
 * <artifactId>amqp-client</artifactId>
 * <version>3.4.1</version>
 * </dependency>
 */
@Slf4j
public class Sender {


    public static void main(String[] args) throws Exception {
        send01();
        send02();
    }

    /*** 简单队列发送消息 */
    private static void send01() throws Exception {
        // ----- 获取连接与通道
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();
        // ----- 声明队列
        channel.queueDeclare(SIMPLE_QUEUE_NAME, FALSE, FALSE, FALSE, null);
        // ----- 发送消息
        String message = "hello world111!";
        channel.basicPublish("", SIMPLE_QUEUE_NAME, null, message.getBytes());
        log.info("[sender] simple queue sender, message: {}", message);
        // ----- 释放资源
        Constant.close(connection, channel);
    }

    /*** work模式：一个生产者，多个消费者，但是一个消息只能被一个消费者消费 */
    private static void send02() throws Exception {
        // ----- 获取连接与通道
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();
        // ----- 声明队列
        channel.queueDeclare(WORK_QUEUE_NAME, FALSE, FALSE, FALSE, null);
        for (int i = 0; i < 100; i++) {
            String message = "啦啦啦德玛西亚" + i;
            channel.basicPublish("", WORK_QUEUE_NAME, null, message.getBytes());
            log.info("[sender] work queue sender, message: {}", message);
            TimeUnit.MILLISECONDS.sleep(i);
        }
        Constant.close(connection, channel);
    }

}
