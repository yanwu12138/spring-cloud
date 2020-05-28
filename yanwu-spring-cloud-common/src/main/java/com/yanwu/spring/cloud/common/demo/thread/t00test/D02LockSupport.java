package com.yanwu.spring.cloud.common.demo.thread.t00test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.LockSupport;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-05-28 19:45:05.
 * <p>
 * describe:
 */
@SuppressWarnings("all")
public class D02LockSupport {
    private static final Random RANDOM = new Random();
    private static Thread addThread = null, sizeThread = null;
    private static final List<Integer> VALUES = new ArrayList<>();

    public static void main(String[] args) {
        D02LockSupport support = new D02LockSupport();
        sizeThread = new Thread(() -> {
            System.out.println("size thread start");
            LockSupport.park();
            System.out.println("size thread end");
            LockSupport.unpark(addThread);
        }, "size-thread");

        addThread = new Thread(() -> {
            System.out.println("add thread start");
            for (int i = 0; i < 10; i++) {
                support.add();
                System.out.println("add thread: " + i);
                if (support.size() == 5) {
                    LockSupport.unpark(sizeThread);
                    LockSupport.park();
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
