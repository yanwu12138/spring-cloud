package com.yanwu.spring.cloud.common.demo.d01java.d01proxy.d01static;

/**
 * @author Baofeng Xu
 * @date 2020/9/22 13:43.
 * <p>
 * description: 发送短信的接口
 */
public interface SmsService {

    /**
     * 发送信息
     *
     * @param message 信息
     * @return 结果
     */
    String send(String message);
}
