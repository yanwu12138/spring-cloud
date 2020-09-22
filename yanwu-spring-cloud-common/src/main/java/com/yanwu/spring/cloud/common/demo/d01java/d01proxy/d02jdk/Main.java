package com.yanwu.spring.cloud.common.demo.d01java.d01proxy.d02jdk;

/**
 * @author Baofeng Xu
 * @date 2020/9/22 14:07.
 * <p>
 * description:
 */
public class Main {
    public static void main(String[] args) {
        SmsService proxy = (SmsService) JdkProxyFactory.getProxy(new SmsServiceImpl());
        proxy.send("hello world");
    }
}
