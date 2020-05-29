package com.yanwu.spring.cloud.common.demo.thread.t00test;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/29 10:11.
 * <p>
 * description:
 */
@SuppressWarnings("all")
public class D22ReentrantLock {
    private static final Integer[] NUMBERS = {
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
            15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26
    };
    private static final Character[] CHARS = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };
    private static final ReentrantLock LOCK = new ReentrantLock();
    private static final Condition NUMB_CONDITION = LOCK.newCondition();
    private static final Condition CHAR_CONDITION = LOCK.newCondition();

    public static void main(String[] args) {
        new Thread(() -> {
            for (Integer i : NUMBERS) {
                LOCK.lock();
                try {
                    System.out.println(Thread.currentThread().getName() + " > " + i);
                    CHAR_CONDITION.signal();
                    NUMB_CONDITION.await();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    LOCK.unlock();
                }
            }
        }, "numb").start();
        new Thread(() -> {
            for (Character c : CHARS) {
                LOCK.lock();
                try {
                    System.out.println(Thread.currentThread().getName() + " > " + c);
                    NUMB_CONDITION.signal();
                    CHAR_CONDITION.await();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    LOCK.unlock();
                }
            }
        }, "char").start();
    }
}
