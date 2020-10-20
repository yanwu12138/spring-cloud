package com.yanwu.spring.cloud.message.config.rocket;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author Baofeng Xu
 * @date 2020/10/20 9:20.
 * <p>
 * description:
 */
@Slf4j
@Configuration
public abstract class RocketConsumerConfiguration {

    @Value("${rocketmq.consumer.groupName}")
    private String groupName;
    @Value("${rocketmq.consumer.namesrvAddr}")
    private String nameSrvAddr;
    @Value("${rocketmq.consumer.consumeThreadMin}")
    private Integer consumeThreadMin;
    @Value("${rocketmq.consumer.consumeThreadMax}")
    private Integer consumeThreadMax;
    @Value("${rocketmq.consumer.consumeMessageBatchMaxSize}")
    private Integer consumeMessageBatchMaxSize;

    /**
     * mq 消费者配置
     */
    public void listener(String topic, String tag) throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(groupName);
        consumer.setNamesrvAddr(nameSrvAddr);
        consumer.setConsumeThreadMin(consumeThreadMin);
        consumer.setConsumeThreadMax(consumeThreadMax);
        consumer.setConsumeMessageBatchMaxSize(consumeMessageBatchMaxSize);
        // ----- 设置consumer第一次启动是从队列头部开始还是队列尾部开始, 如果不是第一次启动，那么按照上次消费的位置继续消费
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        // ----- 设置消费模型，集群还是广播，默认为集群
        consumer.setMessageModel(MessageModel.CLUSTERING);
        consumer.subscribe(topic, tag);
        // ----- 设置监听
        consumer.registerMessageListener((MessageListenerConcurrently) (messages, context) -> RocketConsumerConfiguration.this.dealBody(messages));
        consumer.start();
        log.info("rocket mq consumer server start success. groupName: {}, nameSrvAddr: {}", groupName, nameSrvAddr);
    }

    /**
     * 处理body的业务
     *
     * @param messages 消息
     * @return 消费状态
     */
    public abstract ConsumeConcurrentlyStatus dealBody(List<MessageExt> messages);

}
