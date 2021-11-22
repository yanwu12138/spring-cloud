package com.yanwu.spring.cloud.common.rocket;

import com.aliyun.openservices.ons.api.OnExceptionContext;
import lombok.Data;
import lombok.ToString;

/**
 * @author Baofeng Xu
 * @date 2021/11/22 11:59.
 * <p>
 * description:
 */
@Data
@ToString
public class WrapperExceptionContext {

    private String messageId;

    private String topic;

    private Throwable throwable;

    public static WrapperExceptionContext wrapper(OnExceptionContext context) {
        WrapperExceptionContext exceptionContext = new WrapperExceptionContext();
        exceptionContext.setThrowable(context.getException());
        exceptionContext.setMessageId(context.getMessageId());
        exceptionContext.setTopic(context.getTopic());
        return exceptionContext;
    }
}
