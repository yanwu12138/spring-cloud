package com.yanwu.spring.cloud.common.demo.thread.t06sync;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/28 14:29.
 * <p>
 * description: 自增类
 */
@SuppressWarnings("all")
public class D021AtomicInteger {

    private AtomicInteger atomicInteger = new AtomicInteger();

    public static void main(String[] args) {
        D021AtomicInteger atomic = new D021AtomicInteger();
        Thread[] threads = new Thread[100];
        for (int i = 0; i < 100; i++) {
            threads[i] = new Thread(atomic::test);
        }
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println(atomic.atomicInteger);
    }

    private void test() {
        for (int i = 1000; i > 0; i--) {
            atomicInteger.incrementAndGet();
        }
    }

}
