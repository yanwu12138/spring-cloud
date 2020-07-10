package com.yanwu.spring.cloud.common.demo.d06algorithm;

import java.util.Random;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-07-04 21:59:51.
 * <p>
 * describe:
 */
public class A00Utils {
    private static final Integer MAX_VALUE = 100_000;
    private static final Integer MAX_SIZE = 10_000;
    private static final Random RANDOM = new Random();

    /**
     * 随机生成数组
     *
     * @return 数组
     */
    public static int[] array() {
        int[] array = new int[RANDOM.nextInt(MAX_SIZE)];
        for (int i = 0; i < array.length - 1; i++) {
            array[i] = RANDOM.nextInt(MAX_VALUE);
        }
        return array;
    }

}
