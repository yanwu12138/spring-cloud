package com.yanwu.spring.cloud.common.demo.d06algorithm;

import java.util.Random;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-07-04 21:59:51.
 * <p>
 * describe:
 */
public class S00Utils {
    private static final Integer MAX_VALUE = 1_0000;
    private static final Integer MAX_SIZE = 1_000;
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

    /**
     * 将数组中的两个位置的数据进行交换
     *
     * @param arr 数组
     * @param i   角标i
     * @param j   角标j
     */
    public static void swap(int[] arr, int i, int j) {
        if (i < 0 || i > arr.length - 1 || j < 0 || j > arr.length - 1 || i == j || arr[i] == arr[j]) {
            return;
        }
        arr[i] = arr[i] ^ arr[j];
        arr[j] = arr[i] ^ arr[j];
        arr[i] = arr[i] ^ arr[j];
    }

}
