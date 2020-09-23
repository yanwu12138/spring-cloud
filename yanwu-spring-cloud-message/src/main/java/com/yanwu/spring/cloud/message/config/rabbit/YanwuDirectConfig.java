package com.yanwu.spring.cloud.message.config.rabbit;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.yanwu.spring.cloud.common.core.common.Contents.Message.*;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/14 9:50.
 * <p>
 * description: 路由模式生产者
 */
@Configuration
public class YanwuDirectConfig {

    /**
     * 队列：test_direct_queue_
     *
     * @return queue
     */
    @Bean
    public Queue directQueue() {
        /*
         * durable: 是否持久化，默认是false
         *     持久化队列：会被存储在子攀上，当消息代理重启时仍然存在
         *     暂存队列：当前连接有效
         * exclusive：默认false，只能被当前创建的连接使用，而且当前连接关闭后队列删除
         * autoDelete：是否自动删除，当没有生产者或消费者使用此队列，队列自动删除
         * 一般只需要设置队列的持久化，其余两个就是默认的false
         */
        return new Queue(DIRECT_QUEUE_NAME, TRUE);
    }

    /**
     * 交换机：test_direct_exchange
     *
     * @return exchange
     */
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(DIRECT_EXCHANGE_NAME, TRUE, FALSE);
    }

    /**
     * 绑定：将交换机和队列进行绑定，并设置匹配键 - testDirectRouting
     *
     * @return binding
     */
    @Bean
    public Binding directBinding() {
        return BindingBuilder.bind(directQueue()).to(directExchange()).with(TEST_DIRECT_ROUTING);
    }
}
