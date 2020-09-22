package com.yanwu.spring.cloud.message.demo.rocketmq;

import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.common.message.Message;
import com.yanwu.spring.cloud.common.utils.JsonUtil;
import com.yanwu.spring.cloud.message.bo.MessageBO;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;

/**
 * @author Baofeng Xu
 * @date 2020/9/22 15:48.
 * <p>
 * description: 消息生产者
 */
@Slf4j
public class Sender {

    public static void main(String[] args) throws Exception {
        // ----- 创建一个消息生产者，并设置一个消息生产者组
        DefaultMQProducer producer = new DefaultMQProducer("yanwu_producer_group");
        // ----- 指定nameServer地址
        producer.setNamesrvAddr("39.97.229.71:9876");
        // ----- 初始化producer
        producer.start();
        log.info("sender start success...");
        for (int i = 0; i < 100; i++) {
            MessageBO<String> messageBO = new MessageBO<>();
            messageBO.setMessageId(String.valueOf(i)).setData("hello message: " + i)
                    .setCreate(new Timestamp(System.currentTimeMillis()));
            // ----- topic: 主题; tag: 标签; keys: 关键字; body: 消息内容;
            Message message = new Message("yanwu_topic", "yanwu_tag", String.valueOf(i), JsonUtil.toJsonString(messageBO).getBytes());
            // ----- 发送消息并返回结果
            log.info("send message: {}, result: {}", message, producer.send(message));
        }
        // ----- 一旦生产者实例不再被使用则将其关闭，包括清理资源，关闭网络连接等
        producer.shutdown();
    }

}
