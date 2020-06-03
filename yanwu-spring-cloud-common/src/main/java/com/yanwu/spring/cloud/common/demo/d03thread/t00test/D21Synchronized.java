package com.yanwu.spring.cloud.common.demo.d03thread.t00test;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/29 9:17.
 * <p>
 * description:
 * 1A
 * 2B
 * 3C
 * 4D
 * ...
 * 26Z
 */
@SuppressWarnings("all")
public class D21Synchronized {
    private static final Object LOCK = new Object();
    private static final Integer[] NUMBERS = {
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
            15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26
    };
    private static final Character[] CHARS = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    public static void main(String[] args) {
        new Thread(() -> {
            synchronized (LOCK) {
                for (Integer i : NUMBERS) {
                    System.out.println(Thread.currentThread().getName() + " > " + i);
                    try {
                        LOCK.notify();
                        LOCK.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                LOCK.notify();
            }
        }, "numb").start();
        new Thread(() -> {
            synchronized (LOCK) {
                for (Character c : CHARS) {
                    System.out.println(Thread.currentThread().getName() + " > " + c);
                    try {
                        LOCK.notify();
                        LOCK.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                LOCK.notify();
            }
        }, "char").start();
    }
}