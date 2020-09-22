package com.yanwu.spring.cloud.common.demo.d01java.d01proxy.d01static;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Baofeng Xu
 * @date 2020/9/22 13:46.
 * <p>
 * description: 创建代理类并同样实现发送短信的接口
 */
@Slf4j
public class SmsProxy implements SmsService {
    private final SmsService smsService;

    public SmsProxy(SmsService smsService) {
        this.smsService = smsService;
    }

    @Override
    public String send(String message) {
        // ----- 调用方法之前，我们可以添加自己的操作
        log.info("before method send()");
        String result = smsService.send(message);
        // ----- 调用方法之后，我们同样可以添加自己的操作
        log.info("after method send()");
        return result;
    }

}
