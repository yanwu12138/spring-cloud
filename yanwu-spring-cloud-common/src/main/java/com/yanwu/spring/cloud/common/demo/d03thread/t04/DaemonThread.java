package com.yanwu.spring.cloud.common.demo.d03thread.t04;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-04-24 00:31:44.
 * <p>
 * describe:
 */
@Slf4j
public class DaemonThread extends Thread {

    public static void main(String[] args) throws Exception {
        Thread thread = new DaemonThread();
        thread.setDaemon(true);
        thread.start();
        TimeUnit.SECONDS.sleep(2);
        log.info("main done.");
    }

    @Override
    public void run() {
        for (; ; ) {
            try {
                log.info("run running.");
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (Exception e) {
                log.error("run exception: ", e);
                break;
            }
        }
        log.info("run done.");
    }
}
