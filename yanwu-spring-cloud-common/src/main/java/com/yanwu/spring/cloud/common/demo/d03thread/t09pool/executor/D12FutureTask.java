package com.yanwu.spring.cloud.common.demo.d03thread.t09pool.executor;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-05-31 23:04:18.
 * <p>
 * describe:
 */
public class D12FutureTask {

    public static void main(String[] args) throws Exception {
        FutureTask<String> futureTask = new FutureTask<>(() -> {
            TimeUnit.SECONDS.sleep(1);
            return RandomStringUtils.randomAlphabetic(12).toUpperCase();
        });
        new Thread(futureTask).start();
        System.out.println(futureTask.get());
    }

}
