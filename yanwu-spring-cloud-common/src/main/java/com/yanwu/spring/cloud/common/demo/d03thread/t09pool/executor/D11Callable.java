package com.yanwu.spring.cloud.common.demo.d03thread.t09pool.executor;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.concurrent.*;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-05-31 22:35:11.
 * <p>
 * describe:
 */
public class D11Callable {

    public static void main(String[] args) throws Exception {
        Callable<String> callable = () -> {
            TimeUnit.SECONDS.sleep(2);
            return RandomStringUtils.randomAlphabetic(10);
        };
        ExecutorService pool = Executors.newCachedThreadPool();
        Future<String> future = pool.submit(callable);
        // ----- 会阻塞等call函数执行完
        System.out.println(future.get());
        pool.shutdown();
    }

}
