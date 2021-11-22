package com.yanwu.spring.cloud.common.rocket.consumer;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerOrderly;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.yanwu.spring.cloud.common.rocket.WrapperMessage;
import com.yanwu.spring.cloud.common.rocket.config.RocketProperties;
import com.yanwu.spring.cloud.common.rocket.constant.ConsumerActionEnum;
import com.yanwu.spring.cloud.common.rocket.constant.ConsumerTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Baofeng Xu
 * @date 2021/11/22 10:55.
 * <p>
 * description:
 */
@Slf4j
public class RocketConsumerWrapperBean {

    private static final String TAGS_SP = "||";

    private final RocketProperties rocketProperties;

    private final Map<String, WrapperMessageListener> listenerMap;

    private final Map<String, WrapperMessageListener> tagListenerMap = new HashMap<>();

    public RocketConsumerWrapperBean(RocketProperties rocketProperties, Map<String, WrapperMessageListener> listenerMap) {
        this.rocketProperties = rocketProperties;
        this.listenerMap = listenerMap;
    }

    public void init() {
        log.info("init rocket mq consumer start......");
        RocketConsumerConf[] consumer = rocketProperties.getConsumer();
        // 先初始化 tagListenerMap 保证输入的tag都有一个对应的WrapperMessageListener
        for (RocketConsumerConf rocketConsumerConf : consumer) {
            for (RocketConsumerListener listener : rocketConsumerConf.getListeners()) {
                WrapperMessageListener wrapperMessageListener = Optional.ofNullable(listenerMap.get(listener.getListener())).orElseThrow(() -> new RuntimeException("找不到 listener:" + listener.getListener()));
                String[] tags = StringUtils.split(listener.getExpression(), TAGS_SP);
                for (String tag : tags) {
                    tagListenerMap.put(StringUtils.trim(tag), wrapperMessageListener);
                }
            }
        }
        for (RocketConsumerConf rocketConsumerConf : consumer) {
            DefaultMQPushConsumer pushConsumer = new DefaultMQPushConsumer(rocketConsumerConf.getGroup());
            pushConsumer.setNamesrvAddr(rocketProperties.getNameServer());
            pushConsumer.setInstanceName(rocketConsumerConf.getGroup());
            String expression = StringUtils.join(rocketConsumerConf.getListeners().stream()
                    .map(RocketConsumerListener::getExpression)
                    .collect(Collectors.toList()), " " + TAGS_SP + " ");
            try {
                pushConsumer.subscribe(rocketConsumerConf.getTopic(), expression);
                pushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
                ConsumerTypeEnum consumerType = ConsumerTypeEnum.getByStatus(rocketConsumerConf.getConsumerType());
                if (consumerType != null) {
                    switch (consumerType) {
                        case ORDERLY:
                            pushConsumer.registerMessageListener(registerOrderlyListener());
                            break;
                        case CONCURRENTLY:
                            pushConsumer.registerMessageListener(registerConcurrentlyListener());
                            break;
                        default:
                            throw new RuntimeException("找不到对应的ConsumerType" + rocketConsumerConf.getConsumerType());
                    }
                } else {
                    throw new RuntimeException("找不到对应的ConsumerType" + rocketConsumerConf.getConsumerType());
                }

                pushConsumer.start();
            } catch (MQClientException e) {
                log.info("subscribe error topic " + rocketConsumerConf.getTopic() + " expression " + expression, e);
            }
        }
        log.info("init rocket mq consumer done......");
    }

    private MessageListenerConcurrently registerConcurrentlyListener() {
        return (list, context) -> {
            for (MessageExt message : list) {
                WrapperMessageListener messageListener = tagListenerMap.get(message.getTags());
                if (messageListener == null) {
                    log.error("topic {}, expression {} not exists listener. messageId: {}", message.getTopic(), message.getTags(), message.getMsgId());
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
                try {
                    WrapperMessage wrapperMessage = WrapperMessage.wrapper(message);
                    log.info("mq reader message, topic: {}, tag: {}, body: {}, messageId: {}, queueId: {}, queueOffset: {}",
                            wrapperMessage.getTopic(), wrapperMessage.getTag(), wrapperMessage.getBody(), message.getMsgId(), message.getQueueId(), message.getQueueOffset());
                    ConsumerActionEnum action = messageListener.consume(wrapperMessage);
                    if (action == null || ConsumerActionEnum.RETRY.equals(action)) {
                        log.error("message {} consume later topic: {}, tag: {}, body: {}", message, wrapperMessage.getTopic(), wrapperMessage.getTag(), wrapperMessage.getBody());
                        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                    }
                } catch (Exception e) {
                    log.error("message {} consume error", message, e);
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        };
    }

    private MessageListenerOrderly registerOrderlyListener() {
        return (list, context) -> {
            for (MessageExt message : list) {
                WrapperMessageListener messageListener = tagListenerMap.get(message.getTags());
                if (messageListener == null) {
                    log.error("topic {}, expression {} not exists listener. messageId: {}", message.getTopic(), message.getTags(), message.getMsgId());
                    return ConsumeOrderlyStatus.SUCCESS;
                }
                try {
                    WrapperMessage wrapperMessage = WrapperMessage.wrapper(message);
                    log.info("mq reader order message topic: {}, tag: {}, body: {}, messageId: {}, queueId: {}, queueOffset: {}",
                            wrapperMessage.getTopic(), wrapperMessage.getTag(), wrapperMessage.getBody(), message.getMsgId(), message.getQueueId(), message.getQueueOffset());
                    ConsumerActionEnum action = messageListener.consume(wrapperMessage);
                    if (action == null || ConsumerActionEnum.RETRY.equals(action)) {
                        log.error("order message {} consume later topic: {}, tag: {}, body: {}", message, wrapperMessage.getTopic(), wrapperMessage.getTag(), wrapperMessage.getBody());
                        return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                    }
                } catch (Exception e) {
                    log.error("order message {} consume error", message, e);
                    return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                }
            }
            return ConsumeOrderlyStatus.SUCCESS;
        };
    }
}
