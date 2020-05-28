package com.yanwu.spring.cloud.common.demo.thread.t06sync;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/28 14:29.
 * <p>
 * description:
 * synchronized：排他锁：偏向锁 >> 自旋锁 >> 重量级锁（系统锁）
 * atomic：CAS乐观锁：通过期望值比对判断是否能够执行写操作
 * longAdder：分段锁：将大批量线程分别划分到不同的数组中，每个数组单独计算，最后汇总所有的数组的结果
 */
@SuppressWarnings("all")
public class D022AtomicAndSynchronizedAndLongAdder {
    private static final Object LOCK = new Object();
    private static int syncCount = 0;
    private static AtomicInteger atomicCount = new AtomicInteger(0);
    private static LongAdder longCount = new LongAdder();

    public static void main(String[] args) throws Exception {
        Thread[] threads = new Thread[1000];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                for (int k = 0; k < 100000; k++) {
                    synchronized (LOCK) {
                        syncCount++;
                    }
                }
            });
        }
        long syncStart = System.currentTimeMillis();
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        long syncEnd = System.currentTimeMillis();
        System.out.println("synchronized: " + syncCount + " time: " + (syncEnd - syncStart));

        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                for (int k = 0; k < 100000; k++) {
                    atomicCount.incrementAndGet();
                }
            });
        }
        long atomicStart = System.currentTimeMillis();
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        long atomicEnd = System.currentTimeMillis();
        System.out.println("atomic: " + atomicCount + " time: " + (atomicEnd - atomicStart));

        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                for (int k = 0; k < 100000; k++) {
                    longCount.increment();
                }
            });
        }
        long longStart = System.currentTimeMillis();
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        long longEnd = System.currentTimeMillis();
        System.out.println("long: " + longCount + " time: " + (longEnd - longStart));
    }
}
