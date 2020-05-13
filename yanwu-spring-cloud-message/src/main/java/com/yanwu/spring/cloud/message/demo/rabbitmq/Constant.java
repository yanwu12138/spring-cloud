package com.yanwu.spring.cloud.message.demo.rabbitmq;

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
    public static final String[] USERS;
    public static final Executor EXECUTOR;
    public static final Boolean TRUE = true;
    public static final Boolean FALSE = false;
    public static final String[] READ_TOPICS;
    public static final String[] SEND_TOPICS;
    public static final String TOPIC = "topic";
    public static final String FANOUT = "fanout";
    public static final String DIRECT = "direct";
    public static final String SIMPLE_QUEUE_NAME = "test_simple_queue";
    public static final String WORK_QUEUE_NAME = "test_work_queue";
    public static final String FANOUT_EXCHANGE_NAME = "test_fanout_exchange";
    public static final String FANOUT_QUEUE_NAME = "test_fanout_queue_";
    public static final String DIRECT_EXCHANGE_NAME = "test_direct_exchange";
    public static final String DIRECT_QUEUE_NAME = "test_direct_queue_";
    public static final String TOPIC_EXCHANGE_NAME = "test_topic_exchange";
    public static final String TOPIC_QUEUE_NAME = "test_topic_queue_";

    static {
        USERS = new String[]{"yanwu", "lotus", "wenxin", "wenfu"};
        READ_TOPICS = new String[]{"yanwu.*", "yanwu.#", "*.*", "#.#"};
        SEND_TOPICS = new String[]{"yanwu.lotos", "yanwu.lotus.love", "lotus.love", "lotus.wenxin.love"};
        EXECUTOR = Executors.newFixedThreadPool(100);
    }
}
