package com.yanwu.spring.cloud.common.core.common;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/8 13:42.
 * <p>
 * description: 常量
 */
public interface Contents {
    String POINT = ".";
    String NUL = "";
    Integer DEFAULT_SIZE = 1024 * 10;
    /*** 用户登陆缓存：KEY ***/
    String LOGIN_TOKEN = "login_token_";
    /*** token ***/
    String TOKEN = "X-Token";
    /*** 每次请求的唯一标识 */
    String TX_ID = "txId";
    /*** 用户登陆缓存：有效期时长（单位：S） ***/
    Integer TOKEN_TIME_OUT = 60 * 60;
    /*** 日志输出 */
    String METHOD = "[Method]";
    String METHOD_TYPE = "[MethodType]";
    String PARAM = "[Param]";
    String RESULT = "[Result]";
    String ERROR = "[Exception]";

    String QR_CODE_EXT = "jpg";

    public static final class Message {
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
        public static final String NON_EXISTENT_EXCHANGE = "non-existent-exchange";
        public static final String FANOUT_EXCHANGE_NAME = "test_fanout_exchange";
        public static final String FANOUT_YANWU_QUEUE_NAME = "test_fanout_yanwu_queue";
        public static final String FANOUT_LOTUS_QUEUE_NAME = "test_fanout_lotus_queue";
        public static final String DIRECT_QUEUE_NAME = "test_direct_queue_";
        public static final String DIRECT_EXCHANGE_NAME = "test_direct_exchange";
        public static final String TEST_DIRECT_ROUTING = "testDirectRouting";
        public static final String TOPIC_EXCHANGE_NAME = "test_topic_exchange";
        public static final String TOPIC_ROUTE_ALL_KEY = "#.#";
        public static final String TOPIC_QUEUE_ALL_QUEUE = "test_topic_all_queue";
        public static final String TOPIC_ROUTE_YANWU_KEY = "yanwu.#";
        public static final String TOPIC_QUEUE_YANWU_QUEUE = "test_topic_yanwu_queue";
        public static final String TOPIC_ROUTE_LOTUS_KEY = "lotus.*";
        public static final String TOPIC_QUEUE_LOTUS_QUEUE = "test_topic_lotus_queue";

        static {
            USERS = new String[]{"yanwu", "lotus", "wenxin", "wenfu"};
            READ_TOPICS = new String[]{"yanwu.*", "yanwu.#", "*.*", "#.#"};
            SEND_TOPICS = new String[]{"yanwu.lotos", "yanwu.lotus.love", "lotus.love", "lotus.wenxin.love"};
            EXECUTOR = Executors.newFixedThreadPool(100);
        }
    }
}
