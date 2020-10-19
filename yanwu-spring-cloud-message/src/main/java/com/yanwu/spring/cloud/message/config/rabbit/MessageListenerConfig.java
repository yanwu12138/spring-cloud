package com.yanwu.spring.cloud.message.config.rabbit;

import com.yanwu.spring.cloud.common.utils.ContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/14 11:35.
 * <p>
 * description:
 */
@Slf4j
@Configuration
public class MessageListenerConfig {

    @Resource
    private CachingConnectionFactory connectionFactory;

    @Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer() {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setConcurrentConsumers(1);
        container.setMaxConcurrentConsumers(1);
        // ----- 将确认模式由自动改为手动
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        // ----- 绑定队列
        container.setQueues(
                ContextUtil.getBean("directQueue", Queue.class),
                ContextUtil.getBean("fanoutYanwuQueue", Queue.class),
                ContextUtil.getBean("fanoutLotusQueue", Queue.class),
                ContextUtil.getBean("topicAllQueue", Queue.class),
                ContextUtil.getBean("topicYanwuQueue", Queue.class),
                ContextUtil.getBean("topicLotusQueue", Queue.class)
        );
        return container;
    }
}
