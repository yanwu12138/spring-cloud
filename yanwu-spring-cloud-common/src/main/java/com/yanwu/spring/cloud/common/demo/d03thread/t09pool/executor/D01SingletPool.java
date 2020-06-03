package com.yanwu.spring.cloud.common.demo.d03thread.t09pool.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-05-31 23:29:11.
 * <p>
 * describe:
 */
public class D01SingletPool {

    public static void main(String[] args) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        System.out.println(executor);
        for (int i = 0; i < 5; i++) {
            final int j = i;
            executor.execute(() -> {
                System.out.println(j + ": " + Thread.currentThread().getName());
            });
        }
        executor.shutdown();
    }

}
