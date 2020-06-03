package com.yanwu.spring.cloud.common.demo.d03thread.t07lock;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/28 15:00.
 * <p>
 * description: 互斥锁 + 可重入锁
 */
@SuppressWarnings("all")
public class D003ReentrantLock implements Runnable {
    /**
     * 通过构造函数可以指定该锁是公平锁还是非公平锁
     * true：公平锁
     * false：非公平锁（默认）
     */
    private static final ReentrantLock LOCK = new ReentrantLock(true);

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            // == 通过lock获取锁
            LOCK.lock();
            try {
                System.out.println(Thread.currentThread().getName() + "获得锁");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                LOCK.unlock();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        D003ReentrantLock reentrant = new D003ReentrantLock();
        Thread thread1 = new Thread(reentrant);
        Thread thread2 = new Thread(reentrant);
        thread1.start();
        thread2.start();
    }
}
