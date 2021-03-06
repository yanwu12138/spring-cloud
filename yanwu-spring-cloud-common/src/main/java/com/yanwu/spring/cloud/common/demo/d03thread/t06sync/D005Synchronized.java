package com.yanwu.spring.cloud.common.demo.d03thread.t06sync;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/28 13:55.
 * <p>
 * description: 静态同步代码块的锁对象是 D05Synchronized.class 对象
 */
@SuppressWarnings("all")
public class D005Synchronized {

    private static final Object LOCK = new Object();
    private static int count = 10;

    public static void main(String[] args) {
        D005Synchronized sync = new D005Synchronized();
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

    private synchronized static void test() {
        count--;
        System.out.println(Thread.currentThread().getName() + " count = " + count);
    }

}
