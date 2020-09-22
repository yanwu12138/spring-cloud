package com.yanwu.spring.cloud.common.demo.d01java.d01proxy.d03cglib;

/**
 * @author Baofeng Xu
 * @date 2020/9/22 14:29.
 * <p>
 * description: CGLIB动态代理测试
 */
public class Main {
    public static void main(String[] args) {
        SmsServiceImpl proxy = (SmsServiceImpl) CglibProxyFactory.getProxy(SmsServiceImpl.class);
        proxy.send("hello world");
    }
}
