package com.yanwu.spring.cloud.common.demo.thread.t00test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-05-28 21:55:14.
 * <p>
 * describe:
 */
public class D01WaitNotify {
    private static final Random RANDOM = new Random();
    private static final List<Integer> VALUES = new ArrayList<>();
    private static final Object LOCK = new Object();

    public static void main(String[] args) {
        D01WaitNotify support = new D01WaitNotify();
        Thread sizeThread = new Thread(() -> {
            synchronized (LOCK) {
                System.out.println("size thread start");
                try {
                    LOCK.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("size thread end");
                LOCK.notify();
            }
        }, "size-thread");

        Thread addThread = new Thread(() -> {
            System.out.println("add thread start");
            synchronized (LOCK) {
                for (int i = 0; i < 10; i++) {
                    support.add();
                    System.out.println("add thread: " + i);
                    if (support.size() == 5) {
                        LOCK.notify();
                        try {
                            LOCK.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
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
