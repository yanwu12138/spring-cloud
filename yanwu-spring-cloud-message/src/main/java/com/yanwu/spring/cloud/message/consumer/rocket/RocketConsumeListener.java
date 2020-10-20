package com.yanwu.spring.cloud.message.consumer.rocket;

import com.yanwu.spring.cloud.message.bo.MessageBO;
import com.yanwu.spring.cloud.message.config.rocket.RocketConsumerConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.List;

/**
 * @author Baofeng Xu
 * @date 2020/10/19 18:03.
 * <p>
 * description:
 */
@Slf4j
@Configuration
public class RocketConsumeListener extends RocketConsumerConfiguration implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${rocketmq.consumer.topic}")
    private String topic;
    @Value("${rocketmq.consumer.tags}")
    private String tags;

    @Override
    public ConsumeConcurrentlyStatus dealBody(List<MessageExt> messages) {
        if (CollectionUtils.isNotEmpty(messages)) {
            messages.forEach(message -> log.info("rocket reader message: {}, topic: {}, tag: {}",
                    MessageBO.getInstance(message.getBody()), message.getTopic(), message.getTags()));
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            super.listener(topic, tags);
        } catch (MQClientException e) {
            log.error("rocket consumer listener failed to start.", e);
        }
    }
}
