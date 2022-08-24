package com.yanwu.spring.cloud.message.controller;

import com.yanwu.spring.cloud.common.pojo.ResponseEnvelope;
import com.yanwu.spring.cloud.common.rocket.WrapperMessage;
import com.yanwu.spring.cloud.common.rocket.producer.RocketProducerWrapperBean;
import com.yanwu.spring.cloud.common.utils.JsonUtil;
import com.yanwu.spring.cloud.message.bo.MessageBO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.Serializable;

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
    private RocketProducerWrapperBean rocketProducerWrapperBean;

    @PostMapping("sender/{topic}/{tag}")
    public ResponseEnvelope<Boolean> direct(@PathVariable("topic") String topic, @PathVariable("tag") String tag,
                                            @RequestBody @Valid MessageBO<T> param) throws Exception {
        WrapperMessage message = new WrapperMessage();
        message.setTag(tag);
        message.setTopic(topic);
        message.setBody(JsonUtil.toString(param));
        rocketProducerWrapperBean.sendOneway(message);
        return ResponseEnvelope.success(Boolean.TRUE);
    }

}
