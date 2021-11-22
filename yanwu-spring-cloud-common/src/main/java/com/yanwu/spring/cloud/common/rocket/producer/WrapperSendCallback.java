package com.yanwu.spring.cloud.common.rocket.producer;

import com.yanwu.spring.cloud.common.rocket.WrapperExceptionContext;
import com.yanwu.spring.cloud.common.rocket.WrapperSendResult;

/**
 * @author Baofeng Xu
 * @date 2021/11/22 11:59.
 * <p>
 * description:
 */
public interface WrapperSendCallback {

    void onSuccess(WrapperSendResult sendResult);

    void onException(WrapperExceptionContext exceptionContext);
}
