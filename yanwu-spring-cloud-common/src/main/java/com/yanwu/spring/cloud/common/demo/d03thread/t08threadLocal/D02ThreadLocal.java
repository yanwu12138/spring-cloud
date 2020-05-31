package com.yanwu.spring.cloud.common.demo.d03thread.t08threadLocal;

import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-05-31 21:08:11.
 * <p>
 * describe: threadLocal 本地线程，将数据保留在本地
 * 即使在第二个线程中set()了THREAD_LOCAL中的数据，但是在第一个线程中依然会输出null，因为threadLocal实现本地
 */
public class D02ThreadLocal {

    private static final ThreadLocal<Person> THREAD_LOCAL = new ThreadLocal<>();

    public static void main(String[] args) {
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(THREAD_LOCAL.get());
        }).start();
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            THREAD_LOCAL.set(new Person());
        }).start();
    }

    private static class Person {
        private String name;
    }
}
