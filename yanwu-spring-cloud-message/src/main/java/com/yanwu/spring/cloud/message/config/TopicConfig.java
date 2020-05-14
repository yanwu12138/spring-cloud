package com.yanwu.spring.cloud.message.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.yanwu.spring.cloud.common.core.common.Contents.Message.*;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/14 10:32.
 * <p>
 * description: 主题模式生产者
 */
@Configuration
public class TopicConfig {

    @Bean
    public Queue topicAllQueue() {
        return new Queue(TOPIC_QUEUE_ALL_QUEUE, TRUE);
    }

    @Bean
    public Queue topicYanwuQueue() {
        return new Queue(TOPIC_QUEUE_YANWU_QUEUE, TRUE);
    }

    @Bean
    public Queue topicLotusQueue() {
        return new Queue(TOPIC_QUEUE_LOTUS_QUEUE, TRUE);
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE_NAME);
    }

    @Bean
    public Binding topicAllBinding() {
        return BindingBuilder.bind(topicAllQueue()).to(topicExchange()).with(TOPIC_ROUTE_ALL_KEY);
    }

    @Bean
    public Binding topicYanwuBinding() {
        return BindingBuilder.bind(topicYanwuQueue()).to(topicExchange()).with(TOPIC_ROUTE_YANWU_KEY);
    }

    @Bean
    public Binding topicLotusBinding() {
        return BindingBuilder.bind(topicLotusQueue()).to(topicExchange()).with(TOPIC_ROUTE_LOTUS_KEY);
    }

}
