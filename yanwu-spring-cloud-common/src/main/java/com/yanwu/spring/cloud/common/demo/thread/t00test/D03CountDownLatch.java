package com.yanwu.spring.cloud.common.demo.thread.t00test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-05-28 21:55:53.
 * <p>
 * describe:
 */
public class D03CountDownLatch {
    private static final Random RANDOM = new Random();
    private static final List<Integer> VALUES = new ArrayList<>();
    private static final CountDownLatch ADD_LATCH = new CountDownLatch(1);
    private static final CountDownLatch SIZE_LATCH = new CountDownLatch(1);

    public static void main(String[] args) {
        D03CountDownLatch latch = new D03CountDownLatch();
        Thread sizeThread = new Thread(() -> {
            System.out.println("size thread start");
            try {
                SIZE_LATCH.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("size thread end");
            ADD_LATCH.countDown();
        }, "size-thread");

        Thread addThread = new Thread(() -> {
            System.out.println("add thread start");
            for (int i = 0; i < 10; i++) {
                latch.add();
                System.out.println("add thread: " + i);
                if (latch.size() == 5) {
                    SIZE_LATCH.countDown();
                    try {
                        ADD_LATCH.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("add thread end");
        }, "add-thread");

        sizeThread.start();
        addThread.start();
    }

    private void add() {
        VALUES.add(RANDOM.nextInt());
    }

    private Integer size() {
        return VALUES.size();
    }
}
