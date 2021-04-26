package com.yanwu.spring.cloud.common.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Baofeng Xu
 * @date 2021/4/26 14:39.
 * <p>
 * description:
 */
@Slf4j
public class ThreadUtil {

    public static void sleep(long sleep) {
        if (sleep <= 1) {
            return;
        }
        try {
            Thread.sleep(sleep);
        } catch (Exception e) {
            log.error("download file speed limits error.", e);
        }
    }

    private ThreadUtil() {
    }
}
