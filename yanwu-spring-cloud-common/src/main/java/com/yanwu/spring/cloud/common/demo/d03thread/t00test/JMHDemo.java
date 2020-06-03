package com.yanwu.spring.cloud.common.demo.d03thread.t00test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-05-30 21:25:01.
 * <p>
 * describe: JMH 测试工具
 */
public class JMHDemo {
    private static final Random RANDOM = new Random();
    private static final List<Integer> NUMS = new ArrayList<>();

    static {
        for (int i = 0; i < 1000; i++) {
            NUMS.add(1000000 + RANDOM.nextInt(1000000));
        }
    }

    private static Boolean isPrime(Integer num) {
        for (int i = 2; i < num / 2; i++) {
            if (num % i == 0) {
                return false;
            }
        }
        return true;
    }

    public static void forEach() {
        NUMS.forEach(num -> {
            isPrime(num);
        });
    }

    public static void parallel() {
        NUMS.parallelStream().forEach(num -> {
            isPrime(num);
        });
    }

}
