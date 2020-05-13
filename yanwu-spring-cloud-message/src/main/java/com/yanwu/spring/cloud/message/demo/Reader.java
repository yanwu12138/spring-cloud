package com.yanwu.spring.cloud.message.demo;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

import static com.yanwu.spring.cloud.message.demo.Constant.*;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/13 16:56.
 * <p>
 * description: 读取消息
 * * 需要导入依赖
 * <dependency>
 * <groupId>com.rabbitmq</groupId>
 * <artifactId>amqp-client</artifactId>
 * <version>3.4.1</version>
 * </dependency>
 */
@Slf4j
@SuppressWarnings("all")
public class Reader {

    public static void main(String[] args) throws Exception {
        read01();
        read02();
    }

    /*** 简单队列的读取消息*/
    private static void read01() throws Exception {
        EXECUTOR.execute(new Runnable() {
            @Override
            @SneakyThrows
            public void run() {
                Connection connection = ConnectionUtil.getConnection();
                Channel channel = connection.createChannel();
                channel.queueDeclare(SIMPLE_QUEUE_NAME, FALSE, FALSE, FALSE, null);
                QueueingConsumer consumer = new QueueingConsumer(channel);
                channel.basicConsume(SIMPLE_QUEUE_NAME, TRUE, consumer);
                while (true) {
                    QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                    String message = new String(delivery.getBody());
                    log.info("[reader] simple queue reader, thread: {}, message: {}", Thread.currentThread().getName(), message);
                }
            }
        });
    }

    /*** work模式：一个生产者，多个消费者，但是一个消息只能被一个消费者消费 */
    private static void read02() throws Exception {
        for (int i = 0; i < 5; i++) {
            EXECUTOR.execute(new Runnable() {
                @Override
                @SneakyThrows
                public void run() {
                    Connection connection = ConnectionUtil.getConnection();
                    Channel channel = connection.createChannel();
                    channel.queueDeclare(WORK_QUEUE_NAME, FALSE, FALSE, FALSE, null);
                    QueueingConsumer consumer = new QueueingConsumer(channel);
                    channel.basicConsume(WORK_QUEUE_NAME, TRUE, consumer);
                    while (true) {
                        QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                        String message = new String(delivery.getBody());
                        log.info("[reader] wrok queue reader, thread: {}, message: {}", Thread.currentThread().getName(), message);
                        TimeUnit.MILLISECONDS.sleep(100);
                    }
                }
            });
        }
    }

}
