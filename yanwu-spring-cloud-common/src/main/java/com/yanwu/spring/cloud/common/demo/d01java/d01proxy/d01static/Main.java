package com.yanwu.spring.cloud.common.demo.d01java.d01proxy.d01static;


/**
 * @author Baofeng Xu
 * @date 2020/9/22 13:49.
 * <p>
 * description: 静态代理测试
 */
public class Main {

    public static void main(String[] args) {
        SmsProxy smsProxy = new SmsProxy(new SmsServiceImpl());
        smsProxy.send("hello world");
    }

}
