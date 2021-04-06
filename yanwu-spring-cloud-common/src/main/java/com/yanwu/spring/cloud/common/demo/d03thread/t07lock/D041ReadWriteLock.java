package com.yanwu.spring.cloud.common.demo.d03thread.t07lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
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
            read();
            System.out.println("read end");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    private void read() {
        System.out.println("read");
    }

    private void write(Lock lock) {
        lock.lock();
        try {
            TimeUnit.SECONDS.sleep(2);
            write();
            System.out.println("write end");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    private void write() {
        System.out.println("write");
    }

    public static void main(String[] args) {
        D041ReadWriteLock temp = new D041ReadWriteLock();
        Runnable read = () -> temp.read(READ_LOCK);
        Runnable write = () -> temp.write(WRITE_LOCK);
        for (int i = 0; i < 2; i++) {
            new Thread(write).start();
        }
        for (int i = 0; i < 8; i++) {
            new Thread(read).start();
        }
    }

}
