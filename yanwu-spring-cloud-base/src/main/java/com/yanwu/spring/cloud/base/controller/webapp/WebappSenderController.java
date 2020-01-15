package com.yanwu.spring.cloud.base.controller.webapp;

import com.yanwu.spring.cloud.common.amqp.RabbitMQSender;
import com.yanwu.spring.cloud.common.core.annotation.LogAndCheckParam;
import com.yanwu.spring.cloud.common.mvc.res.ResponseEnvelope;
import com.yanwu.spring.cloud.common.mvc.vo.base.YanwuUserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-08-03 14:04.
 * <p>
 * description:
 */
@Slf4j
@RestController
@RequestMapping("webapp/sender/")
public class WebappSenderController {

    @Autowired
    private RabbitMQSender sender;

    @LogAndCheckParam
    @PostMapping(value = "sender")
    public ResponseEntity<ResponseEnvelope<Void>> sender(@RequestBody YanwuUserVO yanwuUserVO) throws Exception {
        // ===== test1
        String queueName1 = "test_1";
        sender.convertAndSend(queueName1, yanwuUserVO);
        // ===== test2
        String exchange2 = "test_2";
        String routingKey2 = "test_2";
        sender.convertAndSend(exchange2, routingKey2, yanwuUserVO);
        return new ResponseEntity<>(new ResponseEnvelope<>(), HttpStatus.OK);
    }
}
