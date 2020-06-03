package com.yanwu.spring.cloud.common.demo.d03thread.t06sync;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/28 13:55.
 * <p>
 * description:
 */
@SuppressWarnings("all")
public class D004Synchronized {

    private static final Object LOCK = new Object();
    private static int count = 10;

    public static void main(String[] args) {
        D004Synchronized sync = new D004Synchronized();
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(() -> {
                test();
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

        System.out.println(count);
    }

    private static void test() {
        synchronized (LOCK) {
            count--;
            System.out.println(Thread.currentThread().getName() + " count = " + count);
        }
    }

}
