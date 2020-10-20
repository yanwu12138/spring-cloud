package com.yanwu.spring.cloud.message.config.rocket;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author Baofeng Xu
 * @date 2020/10/19 17:46.
 * <p>
 * description:
 */
@Slf4j
@Configuration
public class RocketProducerConfiguration {

    @Value("${rocketmq.producer.groupName}")
    private String groupName;
    @Value("${rocketmq.producer.namesrvAddr}")
    private String nameSrvAddr;
    @Value("${rocketmq.producer.maxMessageSize}")
    private Integer maxMessageSize;
    @Value("${rocketmq.producer.sendMsgTimeOut}")
    private Integer sendMsgTimeOut;
    @Value("${rocketmq.producer.retryTimesWhenSendFailed}")
    private Integer retryTimesWhenSendFailed;


    /**
     * mq 生成者配置
     *
     * @return DefaultMQProducer
     * @throws MQClientException MQClientException.class
     */
    @Bean
    @ConditionalOnProperty(prefix = "rocketmq.producer", value = {"default"}, havingValue = "true")
    public DefaultMQProducer defaultProducer() throws MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer(groupName);
        producer.setNamesrvAddr(nameSrvAddr);
        producer.setVipChannelEnabled(false);
        producer.setMaxMessageSize(maxMessageSize);
        producer.setSendMsgTimeout(sendMsgTimeOut);
        producer.setRetryTimesWhenSendAsyncFailed(retryTimesWhenSendFailed);
        producer.start();
        log.info("rocket mq producer server start success. groupName: {}, nameSrvAddr: {}, maxMessageSize: {}, sendMsgTimeOut: {}, retryTimesWhenSendFailed: {}",
                groupName, nameSrvAddr, maxMessageSize, sendMsgTimeOut, retryTimesWhenSendFailed);
        return producer;
    }

}
