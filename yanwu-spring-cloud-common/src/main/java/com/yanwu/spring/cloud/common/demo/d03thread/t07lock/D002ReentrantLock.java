package com.yanwu.spring.cloud.common.demo.d03thread.t07lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/28 15:00.
 * <p>
 * description: 互斥锁 + 可重入锁
 */
@SuppressWarnings("all")
public class D002ReentrantLock {
    /**
     * 通过构造函数可以指定该锁是公平锁还是非公平锁
     * true：公平锁
     * false：非公平锁（默认）
     */
    private static final ReentrantLock LOCK = new ReentrantLock();

    private void test1() {
        // == 通过lock获取锁
        LOCK.lock();
        try {
            System.out.println("test1 start");
            TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
            System.out.println("test1 end");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // == 通过unlock释放锁
            LOCK.unlock();
        }
    }

    private void test2() {
        try {
            // == 使用lockInterruptibly可通过interrupt函数打断
            LOCK.lockInterruptibly();
            System.out.println("test2 start");
            TimeUnit.SECONDS.sleep(5);
            System.out.println("test2 end");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // -- 释放锁的时候要根据trylock的结果来判断
            LOCK.unlock();
        }
    }

    public static void main(String[] args) throws Exception {
        D002ReentrantLock reentrant = new D002ReentrantLock();
        new Thread(reentrant::test1).start();
        TimeUnit.SECONDS.sleep(1);
        Thread thread2 = new Thread(reentrant::test2);
        thread2.start();
        TimeUnit.SECONDS.sleep(2);
        thread2.interrupt();
    }
}
