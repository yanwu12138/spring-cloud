package com.yanwu.spring.cloud.message.config.rabbit;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.yanwu.spring.cloud.common.core.common.Contents.Message.*;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/14 11:01.
 * <p>
 * description: 订阅模式生产者
 */
@Configuration
public class YanwuFanoutConfig {

    @Bean
    public Queue fanoutYanwuQueue() {
        return new Queue(FANOUT_YANWU_QUEUE_NAME, TRUE);
    }

    @Bean
    public Queue fanoutLotusQueue() {
        return new Queue(FANOUT_LOTUS_QUEUE_NAME, TRUE);
    }

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE_NAME);
    }

    @Bean
    public Binding fanoutYanwuBinding() {
        return BindingBuilder.bind(fanoutYanwuQueue()).to(fanoutExchange());
    }

    @Bean
    public Binding fanoutLotusBinding() {
        return BindingBuilder.bind(fanoutLotusQueue()).to(fanoutExchange());
    }

}
