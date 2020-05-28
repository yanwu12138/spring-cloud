package com.yanwu.spring.cloud.common.demo.thread.t06sync;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/28 13:28.
 * <p>
 * description: volatile只能保证多个线程间共享变量变更的可见性，并不能保证变量变更的原子性，所以volatile不能替代synchronized
 * synchronized能保证原子性和可见性
 * volatile只能保证可见性，不能保证原子性
 */
@SuppressWarnings("all")
public class D015VolatileAndSynchronized implements Runnable {
    private volatile int count = 100;

    @Override
    public synchronized void run() {
        count--;
        System.out.println(Thread.currentThread().getName() + " count = " + count);
    }

    public static void main(String[] args) {
        D015VolatileAndSynchronized obj = new D015VolatileAndSynchronized();
        for (int i = 0; i < 100; i++) {
            new Thread(obj).start();
        }
    }
}
