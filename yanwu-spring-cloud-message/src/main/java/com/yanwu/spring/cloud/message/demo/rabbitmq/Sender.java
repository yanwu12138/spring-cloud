package com.yanwu.spring.cloud.message.demo.rabbitmq;

import lombok.extern.slf4j.Slf4j;

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
@SuppressWarnings("all")
public class Sender {

//    public static void main(String[] args) throws Exception {
//        send01();
//        send02();
//        send03();
//        send04();
//        send05();
//    }
//
//    /*** 简单队列发送消息 */
//    private static void send01() throws Exception {
//        System.out.println("==================== sender sender ====================");
//        // ----- 获取连接与通道
//        Connection connection = ConnectionUtil.getConnection();
//        Channel channel = connection.createChannel();
//        // ----- 声明队列
//        channel.queueDeclare(SIMPLE_QUEUE_NAME, FALSE, FALSE, FALSE, null);
//        // ----- 发送消息
//        String message = "hello world111!";
//        channel.basicPublish("", SIMPLE_QUEUE_NAME, null, message.getBytes());
//        log.info("[sender] simple queue sender, message: {}", message);
//        // ----- 释放资源
//        ConnectionUtil.close(connection, channel);
//    }
//
//    /*** work模式：一个生产者，多个消费者，但是一个消息只能被一个消费者消费 */
//    private static void send02() throws Exception {
//        System.out.println("==================== work sender ====================");
//        // ----- 获取连接与通道
//        Connection connection = ConnectionUtil.getConnection();
//        Channel channel = connection.createChannel();
//        // ----- 声明队列
//        channel.queueDeclare(WORK_QUEUE_NAME, FALSE, FALSE, FALSE, null);
//        for (int i = 0; i < 10; i++) {
//            String message = "啦啦啦德玛西亚" + i;
//            channel.basicPublish("", WORK_QUEUE_NAME, null, message.getBytes());
//            log.info("[sender] work queue sender, message: {}", message);
//            TimeUnit.MILLISECONDS.sleep(i);
//        }
//        ConnectionUtil.close(connection, channel);
//    }
//
//    /*** 订阅模式：将消息发送到交换机，由交换机发送到对应的多个队列 */
//    private static void send03() throws Exception {
//        System.out.println("==================== fanout sender ====================");
//        Connection connection = ConnectionUtil.getConnection();
//        Channel channel = connection.createChannel();
//        channel.exchangeDeclare(FANOUT_EXCHANGE_NAME, FANOUT);
//        String message = "啦啦啦艾希射啦";
//        channel.basicPublish(FANOUT_EXCHANGE_NAME, "", null, message.getBytes());
//        log.info("[sender] fanout exchange sender, message: {}", message);
//        ConnectionUtil.close(connection, channel);
//    }
//
//    /*** 路由模式 */
//    private static void send04() throws Exception {
//        System.out.println("==================== direct sender ====================");
//        Connection connection = ConnectionUtil.getConnection();
//        Channel channel = connection.createChannel();
//        channel.exchangeDeclare(DIRECT_EXCHANGE_NAME, DIRECT);
//        String message = "啦啦啦寡妇不见了";
//        String user = ConnectionUtil.radomeUser();
//        channel.basicPublish(DIRECT_EXCHANGE_NAME, user, null, message.getBytes());
//        log.info("[sender] direct exchange sender, user: {}, message: {}", user, message);
//        ConnectionUtil.close(connection, channel);
//    }
//
//    /*** 主题模式 */
//    private static void send05() throws Exception {
//        System.out.println("==================== topic sender ====================");
//        Connection connection = ConnectionUtil.getConnection();
//        Channel channel = connection.createChannel();
//        channel.exchangeDeclare(TOPIC_EXCHANGE_NAME, TOPIC);
//        String message = "啦啦啦木木哭啦";
//        String topic = ConnectionUtil.radomeTopic();
//        channel.basicPublish(TOPIC_EXCHANGE_NAME, topic, null, message.getBytes());
//        log.info("[sender] topic exchange sender, topic: {}, message: {}", topic, message);
//        ConnectionUtil.close(connection, channel);
//    }

}
