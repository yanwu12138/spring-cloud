package com.yanwu.spring.cloud.message.demo.rocketmq;

//import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
//import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
//import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
//import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
//import com.alibaba.rocketmq.common.message.MessageExt;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

/**
 * @author Baofeng Xu
 * @date 2020/9/22 15:49.
 * <p>
 * description: 消息消费者
 */
@Slf4j
public class Reader {

//    public static void main(String[] args) throws Exception {
//        // ----- 创建一个消息消费者，并设置一个消息消费者组
//        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("yanwu_consumer_group");
//        // ----- 指定nameServer地址
//        consumer.setNamesrvAddr("39.97.229.71:9876");
//        // ----- 设置 Consumer 第一次启动时从队列头部开始消费还是队列尾部开始消费
//        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
//        // ----- 订阅指定 Topic 下的所有消息
//        consumer.subscribe("yanwu_topic", "yanwu_tag || lotus_tag");
//        // ----- 注册消息监听器
//        consumer.registerMessageListener((MessageListenerConcurrently) (list, context) -> {
//            // ----- 默认 list 里只有一条消息，可以通过设置参数来批量接收消息
//            if (CollectionUtils.isNotEmpty(list)) {
//                for (MessageExt ext : list) {
//                    log.info("read message: {}", new String(ext.getBody()));
//                }
//            }
//            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
//        });
//        // ----- 消费者对象在使用之前必须要调用 start 初始化
//        consumer.start();
//        log.info("reader start success...");
//    }

}
