package com.yanwu.spring.cloud.common.demo.d03thread.t08threadLocal;

import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-05-31 20:58:40.
 * <p>
 * describe: threadLocal测试1：
 */
public class D01ThreadLocal {

    volatile static Person person = new Person();

    public static void main(String[] args) {
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(person.name);
        }).start();
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            person.name = "lotus";
        }).start();
    }


    private static class Person {
        public String name = "yanwu";
    }
}
