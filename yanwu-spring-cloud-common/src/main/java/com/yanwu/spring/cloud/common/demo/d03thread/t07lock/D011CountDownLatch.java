package com.yanwu.spring.cloud.common.demo.d03thread.t07lock;

import java.util.concurrent.CountDownLatch;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/28 15:28.
 * <p>
 * description: 倒数计数门栓：当倒数为0时，所有线程才继续往下执行，与join功能类似
 */
@SuppressWarnings("all")
public class D011CountDownLatch {

    public static void main(String[] args) {
        testJoin();
        testCountDownLatch();
    }

    private static void testJoin() {
        Thread[] threads = new Thread[100];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                int result = 0;
                for (int j = 0; j < 10000; j++) {
                    result += j;
                }
            });
        }
        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("join end");
    }

    private static void testCountDownLatch() {
        Thread[] threads = new Thread[100];
        CountDownLatch countDownLatch = new CountDownLatch(threads.length);
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                int result = 0;
                for (int j = 0; j < 10000; j++) {
                    result += j;
                }
                countDownLatch.countDown();
            });
        }
        for (Thread thread : threads) {
            thread.start();
        }
        try {
            countDownLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("count down latch end");
    }
}
