package com.yanwu.spring.cloud.common.demo.thread.t07lock;

import java.util.Random;
import java.util.concurrent.Semaphore;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/28 16:25.
 * <p>
 * description:
 * 信号灯
 */
@SuppressWarnings("all")
public class D051Semaphore {
    private static final Random RANDOM = new Random();
    private static final Semaphore SEMAPHORE = new Semaphore(2, true);

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    SEMAPHORE.acquire();
                    System.out.println(Thread.currentThread().getName() + " start...");
                    Thread.sleep(RANDOM.nextInt(5) * 1000);
                    System.out.println(Thread.currentThread().getName() + " end...");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    SEMAPHORE.release();
                }
            }).start();
        }
    }
}
