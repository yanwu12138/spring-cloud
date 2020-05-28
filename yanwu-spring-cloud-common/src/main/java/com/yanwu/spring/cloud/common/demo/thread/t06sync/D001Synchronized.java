package com.yanwu.spring.cloud.common.demo.thread.t06sync;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/28 13:46.
 * <p>
 * description: 同步方法, 使用的锁对象是this
 */
@SuppressWarnings("all")
public class D001Synchronized {

    private int count = 10;

    public static void main(String[] args) {
        D001Synchronized sync = new D001Synchronized();
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

    private synchronized void test() {
        count--;
        System.out.println(Thread.currentThread().getName() + " count = " + count);
    }
}
