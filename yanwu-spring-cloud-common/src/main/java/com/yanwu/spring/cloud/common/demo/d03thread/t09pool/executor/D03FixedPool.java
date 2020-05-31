package com.yanwu.spring.cloud.common.demo.d03thread.t09pool.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-05-31 23:43:40.
 * <p>
 * describe:
 */
public class D03FixedPool {
    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        System.out.println(executor);
        for (int i = 0; i < 10; i++) {
            final int j = i;
            executor.execute(() -> {
                System.out.println(j + ": " + Thread.currentThread().getName());
            });
        }
        TimeUnit.SECONDS.sleep(1);
        System.out.println(executor);
        executor.shutdown();
    }
}
