package com.yanwu.spring.cloud.common.demo.thread.t04;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-04-24 00:12:06.
 * <p>
 * describe: 死锁
 */
@Slf4j
public class DeadLockThread {
    public static final Object LOCK1 = new Object();
    public static final Object LOCK2 = new Object();

    public static void main(String[] args) {
        new Thread(new DeadLock1()).start();
        new Thread(new DeadLock2()).start();
        log.info("main done");
    }
}

@Slf4j
class DeadLock1 implements Runnable {
    @Override
    public void run() {
        // ----- 先拿LOCK1
        synchronized (DeadLockThread.LOCK1) {
            log.info("DieLock1 start...");
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (Exception e) {
                log.info("DieLock1 exception: ", e);
            }
            // ----- 再拿LOCK2
            synchronized (DeadLockThread.LOCK2) {
                log.info("DieLock1 Running...");
            }
        }
        log.info("DieLock1 stop...");
    }
}

@Slf4j
class DeadLock2 implements Runnable {
    @Override
    public void run() {
        // ----- 先拿LOCK2
        synchronized (DeadLockThread.LOCK2) {
            log.info("DieLock2 start...");
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (Exception e) {
                log.info("DieLock2 exception: ", e);
            }
            // ----- 再拿LOCK1
            synchronized (DeadLockThread.LOCK1) {
                log.info("DieLock2 Running...");
            }
        }
        log.info("DieLock2 stop...");
    }
}