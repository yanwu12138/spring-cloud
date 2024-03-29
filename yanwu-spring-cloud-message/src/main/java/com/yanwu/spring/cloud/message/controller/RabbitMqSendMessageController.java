package com.yanwu.spring.cloud.message.controller;

import com.yanwu.spring.cloud.common.pojo.Result;
import com.yanwu.spring.cloud.common.utils.JsonUtil;
import com.yanwu.spring.cloud.message.bo.MessageBO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.Serializable;
import java.sql.Timestamp;

import static com.yanwu.spring.cloud.common.core.common.Contents.Message.*;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/14 10:02.
 * <p>
 * description:
 */
@Slf4j
@RestController
@RequestMapping("/send/rabbit/message/")
public class RabbitMqSendMessageController<T extends Serializable> {

    @Resource
    private RabbitTemplate template;

    @PostMapping("direct/sender")
    public Result<Boolean> direct(@RequestBody @Valid MessageBO<T> param) {
        sender(DIRECT_EXCHANGE_NAME, TEST_DIRECT_ROUTING, param);
        return Result.success(Boolean.TRUE);
    }

    @PostMapping("topic/sender/{route}")
    public Result<Boolean> topic(@PathVariable("route") String route,
                                 @RequestBody @Valid MessageBO<T> param) {
        sender(TOPIC_EXCHANGE_NAME, route, param);
        return Result.success(Boolean.TRUE);
    }

    @PostMapping("fanout/sender")
    public Result<Boolean> fanout(@RequestBody @Valid MessageBO<T> param) {
        sender(FANOUT_EXCHANGE_NAME, null, param);
        return Result.success(Boolean.TRUE);
    }

    @PostMapping("error/sender")
    public Result<Boolean> error(@RequestBody @Valid MessageBO<T> param) {
        sender(NON_EXISTENT_EXCHANGE, null, param);
        return Result.success(Boolean.TRUE);
    }

    private void sender(String exchange, String routing, MessageBO<T> param) {
        param.setCreate(new Timestamp(System.currentTimeMillis()));
        log.info("sender, exchange: {}, routing: {}, param: {}", exchange, routing, param);
        template.convertAndSend(exchange, routing, JsonUtil.toString(param));
    }

}
