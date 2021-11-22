package com.yanwu.spring.cloud.common.rocket.consumer;

import com.yanwu.spring.cloud.common.rocket.constant.ConsumerActionEnum;
import com.yanwu.spring.cloud.common.rocket.WrapperMessage;

/**
 * @author Baofeng Xu
 * @date 2021/11/22 10:57.
 * <p>
 * description:
 */
public interface WrapperMessageListener {

    /**
     * 消息消费
     * @param message
     * @return
     */
    ConsumerActionEnum consume(WrapperMessage message);

}
