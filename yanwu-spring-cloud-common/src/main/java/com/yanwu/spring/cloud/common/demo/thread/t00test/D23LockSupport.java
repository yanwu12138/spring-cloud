package com.yanwu.spring.cloud.common.demo.thread.t00test;

import java.util.concurrent.locks.LockSupport;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/29 10:19.
 * <p>
 * description:
 */
@SuppressWarnings("all")
public class D23LockSupport {
    private static Thread numbThread = null, charThread = null;
    private static final Integer[] NUMBERS = {
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
            15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26
    };
    private static final Character[] CHARS = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    public static void main(String[] args) {
        numbThread = new Thread(() -> {
            for (Integer i : NUMBERS) {
                System.out.println(Thread.currentThread().getName() + " > " + i);
                LockSupport.unpark(charThread);
                LockSupport.park();
            }
        }, "numb");
        charThread = new Thread(() -> {
            for (Character c : CHARS) {
                System.out.println(Thread.currentThread().getName() + " > " + c);
                LockSupport.unpark(numbThread);
                LockSupport.park();
            }
        }, "char");
        numbThread.start();
        charThread.start();
    }
}
