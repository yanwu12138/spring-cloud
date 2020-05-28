package com.yanwu.spring.cloud.common.demo.thread.t07lock;

import java.util.concurrent.CyclicBarrier;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/28 15:41.
 * <p>
 * description: 循环栅栏：当达到指定的线程数才执行相应的操作，
 * 当没达到要求时，barrier的操作阻塞
 * 当达到要求后，barrier操作执行，然后开始执行下一个循环
 */
@SuppressWarnings("all")
public class D021CyclicBarrier {
    public static void main(String[] args) {
        CyclicBarrier barrier = new CyclicBarrier(20, () -> {
            System.out.println("达到要求");
        });
        for (int i = 0; i < 100; i++) {
            final int finalI = i;
            new Thread(() -> {
                try {
                    barrier.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
