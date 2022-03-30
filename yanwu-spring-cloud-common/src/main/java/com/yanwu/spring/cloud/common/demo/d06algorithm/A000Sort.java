package com.yanwu.spring.cloud.common.demo.d06algorithm;

import com.yanwu.spring.cloud.common.utils.ArrayUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-07-04 22:23:19.
 * <p>
 * describe: 排序
 */
@Slf4j
public class A000Sort {

    public static void main(String[] args) {
        int[] array = A00Utils.array();
        log.info("初始数据: {}", array);

        int[] bubble = ArrayUtil.copyArray(array);
        long bubbleStart = System.nanoTime();
        bubbleSort(bubble);
        log.info("冒泡排序: {} >> {}", bubble, (System.nanoTime() - bubbleStart));

        int[] selection = ArrayUtil.copyArray(array);
        long selectionStart = System.nanoTime();
        selectionSort(selection);
        log.info("选择排序: {} >> {}", selection, (System.nanoTime() - selectionStart));

        int[] insertion = ArrayUtil.copyArray(array);
        long insertionStart = System.nanoTime();
        insertionSort(insertion);
        log.info("插入排序: {} >> {}", insertion, (System.nanoTime() - insertionStart));

        int[] shell = ArrayUtil.copyArray(array);
        long shellStart = System.nanoTime();
        shellSort(shell);
        log.info("希尔排序: {} >> {}", shell, (System.nanoTime() - shellStart));

        int[] merge = ArrayUtil.copyArray(array);
        long mergeStart = System.nanoTime();
        mergeSort(merge);
        log.info("归并排序: {} >> {}", merge, (System.nanoTime() - mergeStart));

        int[] quick = ArrayUtil.copyArray(array);
        long quickStart = System.nanoTime();
        quickSort(quick);
        log.info("快速排序: {} >> {}", quick, (System.nanoTime() - quickStart));

        int[] heap = ArrayUtil.copyArray(array);
        long heapStart = System.nanoTime();
        heapSort(heap);
        log.info("堆　排序: {} >> {}", heap, (System.nanoTime() - heapStart));

        int[] counting = ArrayUtil.copyArray(array);
        long countingStart = System.nanoTime();
        countingSort(counting);
        log.info("计数排序: {} >> {}", counting, (System.nanoTime() - countingStart));

        int[] bucket = ArrayUtil.copyArray(array);
        long bucketStart = System.nanoTime();
        bucketSort(bucket);
        log.info("桶　排序: {} >> {}", bucket, (System.nanoTime() - bucketStart));

        int[] radix = ArrayUtil.copyArray(array);
        long radixStart = System.nanoTime();
        radixSort(radix);
        log.info("基数排序: {} >> {}", radix, (System.nanoTime() - radixStart));
    }

    /**
     * 冒泡排序
     *
     * @param arr 数组
     */
    public static void bubbleSort(int... arr) {
        if (checkArray(arr)) {
            return;
        }
        // ----- 从 0～N 依次进行比较，判断是否需要交换位置
        for (int i = 0; i < arr.length; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                // ----- 当 i > j 时进行位置交换
                if (arr[i] > arr[j]) {
                    ArrayUtil.swap(arr, i, j);
                }
            }
        }
    }

    /**
     * 选择排序
     *
     * @param arr 数组
     */
    public static void selectionSort(int... arr) {
        if (checkArray(arr)) {
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
            ArrayUtil.swap(arr, i, min);
        }
    }

    /**
     * 插入排序
     *
     * @param arr 数组
     */
    public static void insertionSort(int... arr) {
        if (checkArray(arr)) {
            return;
        }
        for (int i = 1; i < arr.length; i++) {
            // ----- 从第 i 的位置往后依次和 i 进行比较，当比 i 小时，将元素往前移动，然后继续该步骤
            for (int j = i - 1; j >= 0 && arr[j] > arr[j + 1]; j--) {
                ArrayUtil.swap(arr, j, j + 1);
            }
        }
    }

    /**
     * 希尔排序
     *
     * @param arr 数组
     */
    public static void shellSort(int... arr) {
        if (checkArray(arr)) {
            return;
        }
        // ----- 步长逐渐减小
        for (int gap = arr.length >> 1; gap > 0; gap >>= 1) {
            // ----- 在同一步长内
            for (int i = gap; i < arr.length; i++) {
                // ----- 同一步长内排序方式是插入排序，temp：待排元素
                int temp = arr[i], j;
                // ----- j-gap：代表有序数组中最大数的下标，j-pag：表示有序数组的前一个元素，减pag是减去偏移量就是步长
                for (j = i; j >= gap && temp < arr[j - gap]; j -= gap) {
                    // ----- 原有序数组最大的后移一位
                    arr[j] = arr[j - gap];
                }
                // ----- 找到了合适的位置插入
                arr[j] = temp;
            }
        }
    }


    /**
     * 归并排序
     *
     * @param arr 数组
     */
    public static void mergeSort(int... arr) {
        if (checkArray(arr)) {
            return;
        }
        mergeSort(arr, new int[arr.length], 0, arr.length - 1);
    }

    /**
     * 递归调用排序
     *
     * @param arr   原数组
     * @param temp  临时数组
     * @param left  起点
     * @param right 终点
     */
    private static void mergeSort(int[] arr, int[] temp, int left, int right) {
        if (left >= right) {
            return;
        }
        // ----- 找到中点，然后分别对左边和右边进行排序操作
        int mid = left + ((right - left) >> 1);
        mergeSort(arr, temp, left, mid);
        mergeSort(arr, temp, mid + 1, right);
        // ----- i：左序列指针  j：右序列指针  t：临时数组指针
        int i = left, j = mid + 1, t = 0;
        while (i <= mid && j <= right) {
            temp[t++] = arr[i] <= arr[j] ? arr[i++] : arr[j++];
        }
        while (i <= mid) {
            // ----- 将左边剩余元素填充进temp中
            temp[t++] = arr[i++];
        }
        while (j <= right) {
            // ----- 将右序列剩余元素填充进temp中
            temp[t++] = arr[j++];
        }
        t = 0;
        // ----- 将temp中的元素全部拷贝到原数组中
        while (left <= right) {
            arr[left++] = temp[t++];
        }
    }

    /**
     * 快速排序
     *
     * @param arr 数组
     */
    public static void quickSort(int... arr) {
        if (checkArray(arr)) {
            return;
        }
        quickSort(arr, 0, arr.length - 1);
    }

    /**
     * 快速排序
     *
     * @param arr   数组
     * @param left  左基准点
     * @param right 右基准点
     */
    private static void quickSort(int[] arr, int left, int right) {
        if (left < right) {
            int index = partition(arr, left, right);
            quickSort(arr, left, index - 1);
            quickSort(arr, index + 1, right);
        }
    }

    /**
     * 找到基准点
     *
     * @param arr   数组
     * @param left  左基准点
     * @param right 右基准点
     * @return 基准点
     */
    private static int partition(int[] arr, int left, int right) {
        int index = left + 1;
        for (int i = index; i <= right; i++) {
            if (arr[i] < arr[left]) {
                ArrayUtil.swap(arr, i, index);
                index++;
            }
        }
        ArrayUtil.swap(arr, left, index - 1);
        return index - 1;
    }

    /**
     * 堆排序
     *
     * @param arr 数组
     */
    public static void heapSort(int... arr) {
        if (checkArray(arr)) {
            return;
        }
        int len = arr.length;
        for (int i = (int) Math.floor(len >> 1); i >= 0; i--) {
            heapify(arr, i, len);
        }
        for (int i = len - 1; i > 0; i--) {
            ArrayUtil.swap(arr, 0, i);
            len--;
            heapify(arr, 0, len);
        }
    }

    private static void heapify(int[] arr, int i, int len) {
        int left = 2 * i + 1, right = 2 * i + 2, largest = i;
        if (left < len && arr[left] > arr[largest]) {
            largest = left;
        }
        if (right < len && arr[right] > arr[largest]) {
            largest = right;
        }
        if (largest != i) {
            ArrayUtil.swap(arr, i, largest);
            heapify(arr, largest, len);
        }
    }

    /**
     * 计数排序
     * *** 缺陷：有可能会产生精度丢失和越界
     *
     * @param arr 数组
     */
    public static void countingSort(int... arr) {
        if (checkArray(arr)) {
            return;
        }
        int maxValue = 0, minValue = 0;
        for (int value : arr) {
            maxValue = Math.max(maxValue, value);
            minValue = Math.min(minValue, value);
        }
        int[] bucket = new int[maxValue - minValue + 1];
        for (int value : arr) {
            bucket[value - minValue]++;
        }
        int sortedIndex = 0;
        for (int j = 0; j < bucket.length; j++) {
            while (bucket[j] > 0) {
                arr[sortedIndex++] = j + minValue;
                bucket[j]--;
            }
        }
    }

    /**
     * 桶排序
     *
     * @param arr 数组
     */
    public static void bucketSort(int... arr) {
        if (checkArray(arr)) {
            return;
        }
        int bucketSize = 10, minValue = arr[0], maxValue = arr[0];
        for (int value : arr) {
            minValue = Math.min(value, minValue);
            maxValue = Math.max(value, maxValue);
        }
        int bucketCount = (maxValue - minValue) / bucketSize + 1;
        int[][] buckets = new int[bucketCount][0];
        // 利用映射函数将数据分配到各个桶中
        for (int item : arr) {
            int index = (maxValue - minValue) / bucketSize;
            buckets[index] = ArrayUtil.arrayAppend(buckets[index], item);
        }
        int arrIndex = 0;
        for (int[] bucket : buckets) {
            if (bucket.length <= 0) {
                continue;
            }
            // 对每个桶进行排序，这里使用了插入排序
            insertionSort(bucket);
            for (int value : bucket) {
                arr[arrIndex++] = value;
            }
        }
    }

    /**
     * @param arr 数组
     */
    public static void radixSort(int... arr) {
        if (checkArray(arr)) {
            return;
        }
        int maxDigit = maxRadix(arr), mod = 10, dev = 1;
        for (int i = 0; i < maxDigit; i++, dev *= 10, mod *= 10) {
            // 考虑负数的情况，这里扩展一倍队列数，其中 [0-9]对应负数，[10-19]对应正数 (bucket + 10)
            int[][] counter = new int[mod * 2][0];
            for (int item : arr) {
                int bucket = ((item % mod) / dev) + mod;
                counter[bucket] = ArrayUtil.arrayAppend(counter[bucket], item);
            }
            int pos = 0;
            for (int[] bucket : counter) {
                for (int value : bucket) {
                    arr[pos++] = value;
                }
            }
        }
    }

    /**
     * 获取最高位数
     */
    private static int maxRadix(int[] arr) {
        int maxValue = ArrayUtil.maxValue(arr);
        if (maxValue == 0) {
            return 1;
        }
        int lenght = 0;
        for (long temp = maxValue; temp != 0; temp /= 10) {
            lenght++;
        }
        return lenght;
    }


    /**
     * 检查数组是否需要进行排序，当数组为null或者数组元素个数小于2时，不进行排序
     *
     * @param arr 数组
     * @return .
     */
    private static boolean checkArray(int... arr) {
        return arr == null || arr.length < 2;
    }
}
