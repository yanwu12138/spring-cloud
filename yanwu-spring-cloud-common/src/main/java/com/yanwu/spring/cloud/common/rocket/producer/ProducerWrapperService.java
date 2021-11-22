package com.yanwu.spring.cloud.common.rocket.producer;

import com.yanwu.spring.cloud.common.rocket.WrapperMessage;
import com.yanwu.spring.cloud.common.rocket.WrapperSendResult;

/**
 * @author Baofeng Xu
 * @date 2021/11/22 11:57.
 * <p>
 * description:
 */
public interface ProducerWrapperService<T> {

    T toMessage(WrapperMessage message);

    WrapperSendResult send(WrapperMessage message);

    void sendOneway(WrapperMessage message);

    void sendAsync(WrapperMessage message, final WrapperSendCallback sendCallback);

}
