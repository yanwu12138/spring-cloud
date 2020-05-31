package com.yanwu.spring.cloud.common.demo.d03thread.t02;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-04-23 22:21:20.
 * <p>
 * describe: 资源共享方式二：runnable实现类的成员变量
 */
@Slf4j
public class MyRunnable implements Runnable {
    private Integer size = 10;

    @Override
    public void run() {
        while (size > 0) {
            log.info("thread: {} is selling size: {}", Thread.currentThread().getName(), size);
            size = size - 1;
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (Exception e) {
                log.error("exception: ", e);
            }
        }
    }

    public static void main(String[] args) {
        Runnable runnable = new MyRunnable();
        new Thread(runnable).start();
        new Thread(runnable).start();
        new Thread(runnable).start();
        new Thread(runnable).start();
        log.info("main done.");
    }
}
