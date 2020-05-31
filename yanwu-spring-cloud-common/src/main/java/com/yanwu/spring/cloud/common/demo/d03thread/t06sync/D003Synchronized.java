package com.yanwu.spring.cloud.common.demo.d03thread.t06sync;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/28 13:55.
 * <p>
 * description:
 */
@SuppressWarnings("all")
public class D003Synchronized {

    private static final Object LOCK = new Object();
    private int count = 10;

    public static void main(String[] args) {
        D003Synchronized sync = new D003Synchronized();
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(sync::test);
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

        System.out.println(sync.count);
    }

    private void test() {
        synchronized (LOCK) {
            count--;
            System.out.println(Thread.currentThread().getName() + " count = " + count);
        }
    }

}
