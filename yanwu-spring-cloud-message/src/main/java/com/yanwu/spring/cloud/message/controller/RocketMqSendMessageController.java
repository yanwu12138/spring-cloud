package com.yanwu.spring.cloud.message.controller;

import com.yanwu.spring.cloud.common.pojo.ResponseEnvelope;
import com.yanwu.spring.cloud.common.utils.JsonUtil;
import com.yanwu.spring.cloud.message.bo.MessageBO;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/14 10:02.
 * <p>
 * description:
 */
@Slf4j
@RestController
@RequestMapping("/send/rocket/message/")
public class RocketMqSendMessageController<T extends Serializable> {

    @Resource
    private DefaultMQProducer rocketProducer;

    @PostMapping("sender/{topic}/{tag}")
    public ResponseEnvelope<Boolean> direct(@PathVariable("topic") String topic, @PathVariable("tag") String tag,
                                            @RequestBody @Valid MessageBO<T> param) throws Exception {
        param.setCreate(new Timestamp(System.currentTimeMillis()));
        Message message = new Message(topic, tag, JsonUtil.toCompactJsonString(param).getBytes());
        log.info("rocket sender message: {}", message);
        SendResult result = rocketProducer.send(message);
        log.info("rocket result message: {}", result);
        return ResponseEnvelope.success(Boolean.TRUE);
    }

}
