package com.yanwu.spring.cloud.message.demo.rabbitmq;

import lombok.extern.slf4j.Slf4j;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/13 16:53.
 * <p>
 * description:
 * * 需要导入依赖
 * <dependency>
 * <groupId>com.rabbitmq</groupId>
 * <artifactId>amqp-client</artifactId>
 * <version>3.4.1</version>
 * </dependency>
 */
@Slf4j
public class ConnectionUtil {

//    public static Connection getConnection() throws Exception {
//        ConnectionFactory factory = new ConnectionFactory();
//        factory.setHost("39.97.229.71");
//        factory.setPort(5672);
//        factory.setUsername("root");
//        factory.setPassword("xbf12138");
//        return factory.newConnection();
//    }
//
//    public static void close(Connection connection, Channel channel) throws Exception {
//        channel.close();
//        connection.close();
//    }
//
//    public static String radomeUser() {
//        int index = RandomUtils.nextInt(0, Contents.Message.USERS.length);
//        return Contents.Message.USERS[index];
//    }
//
//    public static String radomeTopic() {
//        int index = RandomUtils.nextInt(0, Contents.Message.SEND_TOPICS.length);
//        return Contents.Message.SEND_TOPICS[index];
//    }

}
