package com.yanwu.spring.cloud.common.demo.d03thread.t09pool.executor;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-05-31 23:08:25.
 * <p>
 * describe:
 */
public class D13CompletableFuture {
    public static final Random RANDOM = new Random();

    public static void main(String[] args) throws IOException {
        long begin = System.currentTimeMillis();
        CompletableFuture<Double> job01Future = CompletableFuture.supplyAsync(D13CompletableFuture::job01);
        CompletableFuture<Double> job02Future = CompletableFuture.supplyAsync(D13CompletableFuture::job02);
        CompletableFuture<Double> job03Future = CompletableFuture.supplyAsync(D13CompletableFuture::job03);

        job01Future.thenApply(String::valueOf).thenApply(str -> "job01: " + str).thenAccept(System.out::println);
        job02Future.thenApply(String::valueOf).thenApply(str -> "job02: " + str).thenAccept(System.out::println);
        job03Future.thenApply(String::valueOf).thenApply(str -> "job03: " + str).thenAccept(System.out::println);

        CompletableFuture.allOf(job01Future, job02Future, job03Future).join();
        System.out.println("use completable future: " + (System.currentTimeMillis() - begin));
        System.in.read();
    }

    private static Double job01() {
        delay();
        return RANDOM.nextDouble();
    }

    private static double job02() {
        delay();
        return RANDOM.nextDouble();
    }

    private static double job03() {
        delay();
        return RANDOM.nextDouble();
    }

    private static void delay() {
        try {
            int time = RANDOM.nextInt(1000);
            System.out.println(Thread.currentThread().getName() + " sleep: " + time);
            TimeUnit.MILLISECONDS.sleep(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
