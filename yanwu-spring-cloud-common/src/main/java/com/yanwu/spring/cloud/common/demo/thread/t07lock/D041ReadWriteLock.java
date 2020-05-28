package com.yanwu.spring.cloud.common.demo.thread.t07lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/28 16:16.
 * <p>
 * description: 读写锁（共享锁 + 排他锁）
 * 读锁：共享锁：读线程共享
 * 写锁：排他锁：写线程独占
 */
@SuppressWarnings("all")
public class D041ReadWriteLock {
    /*** 互斥锁 ***/
    private static final Lock LOCK = new ReentrantLock();
    /*** 读写锁 ***/
    private static final ReadWriteLock READ_WRITE_LOCK = new ReentrantReadWriteLock();
    /*** 读锁 ***/
    private static final Lock READ_LOCK = READ_WRITE_LOCK.readLock();
    /*** 写锁 ***/
    private static final Lock WRITE_LOCK = READ_WRITE_LOCK.writeLock();

    private void read(Lock lock) {
        lock.lock();
        try {
            TimeUnit.SECONDS.sleep(1);
            System.out.println("read end");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    private void write(Lock lock) {
        lock.lock();
        try {
            TimeUnit.SECONDS.sleep(2);
            System.out.println("write end");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        D041ReadWriteLock temp = new D041ReadWriteLock();
        Runnable read = () -> temp.read(READ_LOCK);
        Runnable write = () -> temp.write(WRITE_LOCK);
        for (int i = 0; i < 18; i++) {
            new Thread(read).start();
        }
        for (int i = 0; i < 2; i++) {
            new Thread(write).start();
        }
    }
}
