package com.yanwu.spring.cloud.common.demo.d01java.d01proxy.d02jdk;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Baofeng Xu
 * @date 2020/9/22 13:44.
 * <p>
 * description: 发送短信的接口实现
 */
@Slf4j
public class SmsServiceImpl implements SmsService {

    @Override
    public String send(String message) {
        log.info("send message: {}", message);
        return message;
    }

}
