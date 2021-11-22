package com.yanwu.spring.cloud.message.consumer.rocket;

import com.yanwu.spring.cloud.common.rocket.WrapperMessage;
import com.yanwu.spring.cloud.common.rocket.constant.ConsumerActionEnum;
import com.yanwu.spring.cloud.common.rocket.consumer.WrapperMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Baofeng Xu
 * @date 2021/11/22 14:25.
 * <p>
 * description:
 */
@Slf4j
@Service("otherListener")
public class RocketOtherListener implements WrapperMessageListener {

    @Override
    public ConsumerActionEnum consume(WrapperMessage message) {
        log.info("other listener, topic: {}, target: {}, key: {}, body: {}", message.getTopic(), message.getTag(), message.getKey(), message.getBody());
        return ConsumerActionEnum.SUCCESS;
    }

}
