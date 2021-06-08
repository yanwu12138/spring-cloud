package com.yanwu.spring.cloud.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author Baofeng Xu
 * @date 2021/4/26 14:39.
 * <p>
 * description:
 */
@Slf4j
public class ThreadUtil {

    private ThreadUtil() {
        throw new UnsupportedOperationException("ThreadUtil should never be instantiated");
    }

    public static void sleep(long sleep) {
        if (sleep <= 1) {
            return;
        }
        try {
            TimeUnit.MILLISECONDS.sleep(sleep);
        } catch (Exception e) {
            log.error("thread: {} sleep error.", Thread.currentThread().getName(), e);
        }
    }
}
