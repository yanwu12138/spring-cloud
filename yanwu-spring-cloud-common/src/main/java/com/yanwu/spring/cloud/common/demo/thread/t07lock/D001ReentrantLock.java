package com.yanwu.spring.cloud.common.demo.thread.t07lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/28 15:00.
 * <p>
 * description: 互斥锁 + 可重入锁
 */
@SuppressWarnings("all")
public class D001ReentrantLock {
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
            TimeUnit.SECONDS.sleep(5);
            System.out.println("test1 end");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // == 通过unlock释放锁
            LOCK.unlock();
        }
    }

    private void test2() {
        boolean locked = false;
        try {
            // == 通过trylock尝试获取锁，不管获取成功还是失败，都会继续执行
            // -- 可以根据trylock的结果来执行对应的业务
            locked = LOCK.tryLock(1, TimeUnit.SECONDS);
            System.out.println("test2 start");
            TimeUnit.SECONDS.sleep(5);
            System.out.println("test2 end");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (locked) {
                // -- 释放锁的时候要根据trylock的结果来判断
                LOCK.unlock();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        D001ReentrantLock reentrant = new D001ReentrantLock();
        new Thread(reentrant::test1).start();
        TimeUnit.SECONDS.sleep(1);
        new Thread(reentrant::test2).start();
    }
}
