package com.yanwu.spring.cloud.common.demo.d03thread.t06sync;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/28 13:28.
 * <p>
 * description: volatile只能保证多个线程间共享变量变更的可见性，并不能保证变量变更的原子性，所以volatile不能替代synchronized
 * synchronized能保证原子性和可见性
 * volatile只能保证可见性，不能保证原子性
 */
@SuppressWarnings("all")
public class D014VolatileNotSynchronized {
    private volatile int count = 0;

    private void test() {
        synchronized (this) {
            for (int i = 0; i < 10000; i++) {
                count++;
            }
        }
    }

    public static void main(String[] args) {
        D014VolatileNotSynchronized obj = new D014VolatileNotSynchronized();
        Thread[] threads = new Thread[100];
        for (int i = 0; i < 100; i++) {
            threads[i] = new Thread(obj::test);
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

        System.out.println(obj.count);
    }
}
