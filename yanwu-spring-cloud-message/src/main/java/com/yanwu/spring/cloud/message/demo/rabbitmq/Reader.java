package com.yanwu.spring.cloud.message.demo.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

import static com.yanwu.spring.cloud.message.demo.rabbitmq.Constant.*;

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
        read03();
        read04();
        read05();
    }

    /*** 简单队列的读取消息*/
    private static void read01() throws Exception {
        System.out.println("==================== simple reader ====================");
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
        System.out.println("==================== work reader ====================");
        for (int i = 0; i < 2; i++) {
            final int finalI = i * 2;
            EXECUTOR.execute(new Runnable() {
                @Override
                @SneakyThrows
                public void run() {
                    Connection connection = ConnectionUtil.getConnection();
                    Channel channel = connection.createChannel();
                    // ----- 限制消费，同一时间只能收到一条消息，只有当消费者响应生产者后，才会进行下一次消费
                    channel.basicQos(1);
                    channel.queueDeclare(WORK_QUEUE_NAME, FALSE, FALSE, FALSE, null);
                    QueueingConsumer consumer = new QueueingConsumer(channel);
                    // ----- FALSE: 表示手动模式确认消息
                    channel.basicConsume(WORK_QUEUE_NAME, FALSE, consumer);
                    while (true) {
                        QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                        String message = new String(delivery.getBody());
                        log.info("[reader] wrok queue reader, thread: {}, message: {}", Thread.currentThread().getName(), message);
                        TimeUnit.MILLISECONDS.sleep(100 * finalI);
                        // ----- 手动确认消息，结合basicQos，将轮询分发改成公平分发
                        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), FALSE);
                    }
                }
            });
        }
    }

    /*** 订阅模式：队列绑定交换机，分别从交换机中取出每一条消息自己消费 */
    private static void read03() throws Exception {
        System.out.println("==================== fanout reader ====================");
        for (int i = 0; i < 3; i++) {
            String queue = FANOUT_QUEUE_NAME + i;
            EXECUTOR.execute(new Runnable() {
                @Override
                @SneakyThrows
                public void run() {
                    Connection connection = ConnectionUtil.getConnection();
                    Channel channel = connection.createChannel();
                    channel.queueDeclare(queue, FALSE, FALSE, FALSE, null);
                    // ----- 绑定到交换机
                    channel.queueBind(queue, FANOUT_EXCHANGE_NAME, "");
                    // ----- 手动模式
                    channel.basicQos(1);
                    QueueingConsumer consumer = new QueueingConsumer(channel);
                    channel.basicConsume(queue, false, consumer);
                    while (true) {
                        QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                        String message = new String(delivery.getBody());
                        log.info("[reader] fanout reader, thread: {}, message: {}", queue, message);
                        TimeUnit.MILLISECONDS.sleep(100);
                        // ----- 手动确认消息，结合basicQos，将轮询分发改成公平分发
                        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), FALSE);
                    }
                }
            });
        }
    }

    /*** 路由模式 */
    private static void read04() throws Exception {
        System.out.println("==================== direct reader ====================");
        for (int i = 0; i < USERS.length; i++) {
            Integer finalI = i;
            String queue = DIRECT_QUEUE_NAME + i;
            EXECUTOR.execute(new Runnable() {
                @Override
                @SneakyThrows
                public void run() {
                    Connection connection = ConnectionUtil.getConnection();
                    Channel channel = connection.createChannel();
                    channel.queueDeclare(queue, FALSE, FALSE, FALSE, null);
                    for (int j = finalI; j < USERS.length; j++) {
                        channel.queueBind(queue, DIRECT_EXCHANGE_NAME, USERS[j]);
                    }
                    channel.basicQos(1);
                    QueueingConsumer consumer = new QueueingConsumer(channel);
                    channel.basicConsume(queue, false, consumer);
                    while (true) {
                        QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                        String message = new String(delivery.getBody());
                        log.info("[reader] direct reader, thread: {}, routing: {}, message: {}", queue, delivery.getEnvelope().getRoutingKey(), message);
                        TimeUnit.MILLISECONDS.sleep(100);
                        // ----- 手动确认消息，结合basicQos，将轮询分发改成公平分发
                        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), FALSE);
                    }
                }
            });
        }
    }

    /*** 主题模式 */
    private static void read05() throws Exception {
        System.out.println("==================== topic reader ====================");
        for (int i = 0; i < READ_TOPICS.length; i++) {
            Integer finalI = i;
            String queue = TOPIC_QUEUE_NAME + i;
            EXECUTOR.execute(new Runnable() {
                @Override
                @SneakyThrows
                public void run() {
                    Connection connection = ConnectionUtil.getConnection();
                    Channel channel = connection.createChannel();
                    channel.queueDeclare(queue, FALSE, FALSE, FALSE, null);
                    channel.queueBind(queue, TOPIC_EXCHANGE_NAME, READ_TOPICS[finalI]);
                    channel.basicQos(1);
                    QueueingConsumer consumer = new QueueingConsumer(channel);
                    channel.basicConsume(queue, false, consumer);
                    while (true) {
                        QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                        String message = new String(delivery.getBody());
                        log.info("[reader] topic reader, thread: {}, routing: {}, message: {}", queue, delivery.getEnvelope().getRoutingKey(), message);
                        TimeUnit.MILLISECONDS.sleep(100);
                        // ----- 手动确认消息，结合basicQos，将轮询分发改成公平分发
                        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), FALSE);
                    }
                }
            });
        }
    }
}
