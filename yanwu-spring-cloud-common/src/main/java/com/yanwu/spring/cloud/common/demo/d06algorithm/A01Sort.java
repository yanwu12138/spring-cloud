package com.yanwu.spring.cloud.common.demo.d06algorithm;

import com.yanwu.spring.cloud.common.utils.ArrayUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-07-04 22:23:19.
 * <p>
 * describe:
 */
@Slf4j
public class A01Sort {

    public static void main(String[] args) {
        int[] array = A00Utils.array();
        log.info("初始数据: {}", array);

        int[] selection = ArrayUtil.copyArray(array);
        long selectionStart = System.nanoTime();
        selectionSort(selection);
        log.info("选择排序: {} : {}", (System.nanoTime() - selectionStart), selection);

        int[] bubble = ArrayUtil.copyArray(array);
        long bubbleStart = System.nanoTime();
        bubbleSort(bubble);
        log.info("冒泡排序: {} : {}", (System.nanoTime() - bubbleStart), bubble);

        int[] insertion = ArrayUtil.copyArray(array);
        long insertionStart = System.nanoTime();
        insertionSort(insertion);
        log.info("插入排序: {} : {}", (System.nanoTime() - insertionStart), insertion);
    }

    /**
     * 选择排序
     *
     * @param arr 数组
     */
    public static void selectionSort(int[] arr) {
        if (arr == null || arr.length < 2) {
            // ----- 当数组为空或者长度小于2时，不用排序，直接返回
            return;
        }
        // ----- 从 0～N 一次进行以下处理
        for (int i = 0; i < arr.length - 1; i++) {
            int min = i;
            // ----- 找到 i～N 中最小的值的下标
            for (int j = i + 1; j < arr.length; j++) {
                min = arr[j] < arr[min] ? j : min;
            }
            // ----- 将最小的值放到i的位置上
            A00Utils.swap(arr, i, min);
        }
    }

    /**
     * 冒泡排序
     *
     * @param arr 数组
     */
    public static void bubbleSort(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }
        // ----- 从 0～N 依次进行比较，判断是否需要交换位置
        for (int i = 0; i < arr.length; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                // ----- 当 i > j 时进行位置交换
                if (arr[i] > arr[j]) {
                    A00Utils.swap(arr, i, j);
                }
            }
        }
    }

    /**
     * 插入排序
     *
     * @param arr 数组
     */
    public static void insertionSort(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }
        for (int i = 1; i < arr.length; i++) {
            // ----- 从第 i 的位置往后依次和 i 进行比较，当比 i 小时，将元素往前移动，然后继续该步骤
            for (int j = i - 1; j >= 0 && arr[j] > arr[j + 1]; j--) {
                A00Utils.swap(arr, j, j + 1);
            }
        }
    }
}
