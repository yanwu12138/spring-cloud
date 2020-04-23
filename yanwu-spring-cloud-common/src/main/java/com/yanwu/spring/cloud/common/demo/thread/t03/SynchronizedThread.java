package com.yanwu.spring.cloud.common.demo.thread.t03;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-04-23 22:31:33.
 * <p>
 * describe:
 */
@Slf4j
public class SynchronizedThread implements Runnable {
    private static final Object LOCK = new Object();
    private Integer size = 10;

    @Override
    public void run() {
        while (size > 0) {
            synchronized (LOCK) {
                if (size > 0) {
                    size = size - 1;
                    log.info("thread: {} is selling size: {}", Thread.currentThread().getName(), size);
                }
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (Exception e) {
                log.error("exception: ", e);
            }
        }
    }

    public static void main(String[] args) {
        Runnable runnable = new SynchronizedThread();
        new Thread(runnable).start();
        new Thread(runnable).start();
        new Thread(runnable).start();
        new Thread(runnable).start();
        log.info("main done.");
    }
}
