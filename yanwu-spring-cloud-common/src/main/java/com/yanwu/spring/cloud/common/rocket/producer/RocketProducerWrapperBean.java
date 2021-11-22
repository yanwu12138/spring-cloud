package com.yanwu.spring.cloud.common.rocket.producer;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.MQProducer;
import com.alibaba.rocketmq.client.producer.SendCallback;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.yanwu.spring.cloud.common.rocket.WrapperExceptionContext;
import com.yanwu.spring.cloud.common.rocket.WrapperMessage;
import com.yanwu.spring.cloud.common.rocket.WrapperSendResult;
import com.yanwu.spring.cloud.common.rocket.config.RocketProperties;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Baofeng Xu
 * @date 2021/11/22 11:25.
 * <p>
 * description:
 */
@Slf4j
public class RocketProducerWrapperBean<T> implements ProducerWrapperService<Message> {

    private final RocketProperties rocketProperties;
    private Map<String, MQProducer> producerMap;
    private Map<String, MQProducer> topicProducerMap;

    public RocketProducerWrapperBean(RocketProperties rocketProperties) {
        this.rocketProperties = rocketProperties;
    }

    public void init() throws MQClientException {
        log.info("init rocket mq producer start......");
        producerMap = new HashMap<>(rocketProperties.getProducer().length);
        topicProducerMap = new HashMap<>(rocketProperties.getProducer().length);
        for (RocketProducerConf producerConf : rocketProperties.getProducer()) {
            MQProducer producer = producerMap.get(producerConf.getGroup());
            if (producer == null) {
                DefaultMQProducer mqProducer = new DefaultMQProducer(producerConf.getGroup());
                mqProducer.setNamesrvAddr(rocketProperties.getNameServer());
                // ----- 发送消息超时
                mqProducer.setSendMsgTimeout(3000);
                // ----- 发送失败后，重试几次
                mqProducer.setRetryTimesWhenSendFailed(2);
                mqProducer.start();
                producer = mqProducer;
                producerMap.put(producerConf.getGroup(), producer);
            }
            topicProducerMap.put(producerConf.getTopic(), producer);
        }
        log.info("init rocket mq producer done......");
    }

    @Override
    public Message toMessage(WrapperMessage message) {
        return new Message(message.getTopic(), message.getTag(), message.getKey(), message.getBodyAsByte());
    }

    @Override
    public WrapperSendResult send(WrapperMessage message) {
        MQProducer producer = getProducer(message.getTopic());
        try {
            SendResult sendResult = producer.send(toMessage(message));
            return wrapper(sendResult);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendOneway(WrapperMessage message) {
        MQProducer producer = getProducer(message.getTopic());
        try {
            producer.sendOneway(toMessage(message));
            log.info("mq sender message, topic: {}, tag: {}, body: {}", message.getTopic(), message.getTag(), message.getBody());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendAsync(final WrapperMessage message, final WrapperSendCallback sendCallback) {
        MQProducer producer = getProducer(message.getTopic());
        try {
            producer.send(toMessage(message), new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    sendCallback.onSuccess(wrapper(sendResult));
                }

                @Override
                public void onException(Throwable throwable) {
                    sendCallback.onException(wrapper(message.getTopic(), throwable));
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected MQProducer getProducer(String topic) {
        MQProducer producer = topicProducerMap.get(topic);
        if (producer == null) {
            throw new RuntimeException("topic " + topic + " not exists producer.");
        }
        return producer;
    }

    private static WrapperSendResult wrapper(SendResult sendResult) {
        WrapperSendResult result = new WrapperSendResult();
        result.setMessageId(sendResult.getMsgId());
        result.setTopic(sendResult.getMessageQueue().getTopic());
        return result;
    }

    private static WrapperExceptionContext wrapper(String topic, Throwable throwable) {
        WrapperExceptionContext exceptionContext = new WrapperExceptionContext();
        exceptionContext.setThrowable(throwable);
        exceptionContext.setTopic(topic);
        return exceptionContext;
    }
}
