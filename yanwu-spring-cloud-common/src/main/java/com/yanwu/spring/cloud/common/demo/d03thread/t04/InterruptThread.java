package com.yanwu.spring.cloud.common.demo.d03thread.t04;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;


/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-04-23 23:54:53.
 * <p>
 * describe: 通知线程终止
 */
@Slf4j
public class InterruptThread {
    public static void main(String[] args) throws Exception {
        Thread1 thread1 = new Thread1();
        thread1.start();
        Thread2 dieLock2 = new Thread2();
        new Thread(dieLock2).start();
        TimeUnit.SECONDS.sleep(2);
        thread1.interrupt();
        dieLock2.flag = false;
        log.info("main done.");
    }
}

@Slf4j
class Thread1 extends Thread {
    @Override
    public void run() {
        while (!interrupted()) {
            log.info("thread1 running...");
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (Exception e) {
                log.error("thread1 exception: ", e);
                break;
            }
        }
        log.info("thread1 stop...");
    }
}

@Slf4j
class Thread2 implements Runnable {
    public volatile Boolean flag = true;

    @Override
    public void run() {
        while (flag) {
            log.info("thread2 running...");
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (Exception e) {
                log.error("thread2 exception: ", e);
                break;
            }
        }
        log.info("thread2 stop...");
    }
}