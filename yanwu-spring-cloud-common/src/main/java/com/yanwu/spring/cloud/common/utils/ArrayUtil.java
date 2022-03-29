package com.yanwu.spring.cloud.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author XuBaofeng.
 * @date 2018-09-28 17:07.
 * <p>
 * description: 数组工具类
 */
@SuppressWarnings("unused")
public class ArrayUtil {

    private ArrayUtil() {
        throw new UnsupportedOperationException("ArrayUtil should never be instantiated");
    }

    /**
     * 当数组为null或者数组中所有的元素全都为null时返回true, 否则返回false
     *
     * @param source 数组
     * @return [true: 空; false: 不为空]
     */
    public static boolean isEmpty(Object[] source) {
        if (source == null || source.length == 0) {
            return true;
        }
        for (Object obj : source) {
            if (obj != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * 数组不为空
     *
     * @param source 数组
     * @return [true: 不为空; false: 空]
     */
    public static boolean isNotEmpty(Object[] source) {
        return !isEmpty(source);
    }

    /**
     * 比较两个数组是否相等
     *
     * @param arrA 数组
     * @param arrB 数组
     * @return [true: 相等; false: 不相等]
     */
    public static boolean isEquals(Object[] arrA, Object[] arrB) {
        if (arrA == null && arrB == null) {
            return true;
        }
        if (arrA == null || arrB == null || arrA.length != arrB.length) {
            return false;
        }
        if (arrA == arrB) {
            return true;
        }
        for (int i = 0; i < arrA.length; i++) {
            if (!(Objects.equals(arrA[i], arrB[i]))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 拷贝数组
     *
     * @param source 元数据
     * @return 拷贝的数据返回
     */
    public static int[] copyArray(int[] source) {
        if (source == null || source.length == 0) {
            return new int[0];
        }
        int[] result = new int[source.length];
        System.arraycopy(source, 0, result, 0, source.length);
        return result;
    }

    /**
     * 删除数组中的null
     *
     * @param source 元数据
     * @return 去除null后的数组
     */
    public static Object[] removeNull(Object[] source) {
        if (source == null) {
            return new Object[0];
        }
        if (source.length <= 1) {
            return source;
        }
        List<Object> result = new ArrayList<>();
        for (Object item : source) {
            if (item != null) {
                result.add(item);
            }
        }
        return result.toArray();
    }

    /**
     * 数组追加
     *
     * @param arr    数组
     * @param values 添加的参数
     * @return 扩容后的数组
     */
    public static int[] arrayAppend(int[] arr, int... values) {
        arr = arr != null ? arr : new int[0];
        int arrLen = arr.length, valLen;
        if (values == null || (valLen = values.length) == 0) {
            return arr;
        }
        arr = Arrays.copyOf(arr, (arrLen + valLen));
        for (int value : values) {
            arr[arrLen++] = value;
        }
        return arr;
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

    /**
     * 二分查找：
     * 找到 value 在 arr 中的位置，当 value 在 arr 中不存在时返回 -1
     *
     * @param arr   数组
     * @param value 值
     * @return 角标
     */
    public static int binarySearch(int[] arr, int value) {
        if (arr == null || arr.length == 0) {
            return -1;
        }
        // ----- 对数组进行排序
        Arrays.sort(arr);
        if (value < arr[0] || value > arr[arr.length - 1]) {
            // ----- 说明 value 不在 arr 中
            return -1;
        }
        int left = 0, right = arr.length - 1, mid;
        while (left < right) {
            // --- 等价于：mid = (left + right) / 2;
            // --- 等价于：mid = left + (right - left) / 2;
            mid = left + ((right - left) >> 1);
            if (arr[mid] == value) {
                // ----- 找到了，返回
                return mid;
            } else if (value > arr[mid]) {
                // ----- 在mid右边
                left = mid + 1;
            } else {
                // ----- 在mid左边
                right = mid - 1;
            }
        }
        // ----- 没找到
        return -1;
    }

    /**
     * 在数组中找到满足 >= value 的最左位置，当找不到时返回 -1
     *
     * @param arr   数组
     * @param value 值
     * @return 角标
     */
    public static int binarySearchNearLeft(int[] arr, int value) {
        if (arr == null || arr.length == 0) {
            return -1;
        }
        // ----- index = -1：记录最左的对号
        int left = 0, right = arr.length - 1, index = -1;
        while (left <= right) {
            // ----- 找到中点位置
            int mid = left + ((right - left) >> 1);
            if (arr[mid] >= value) {
                // ----- 往左找
                index = mid;
                right = mid - 1;
            } else {
                // ----- 往右找
                left = mid + 1;
            }
        }
        return index;
    }

    /**
     * 在数组中找到满足 <= value 的最右位置，当找不到时返回 -1
     *
     * @param arr   数组
     * @param value 值
     * @return 角标
     */
    public static int binarySearchNearRight(int[] arr, int value) {
        if (arr == null || arr.length == 0) {
            return -1;
        }
        // ----- index = -1：记录最右的对号
        int left = 0, right = arr.length - 1, index = -1;
        while (left <= right) {
            // ----- 找到中点位置
            int mid = left + ((right - left) >> 1);
            if (arr[mid] <= value) {
                // ----- 往右找
                index = mid;
                left = mid + 1;
            } else {
                // ----- 往左找
                right = mid - 1;
            }
        }
        return index;
    }

    /**
     * 找到数组中最大的数
     *
     * @param arr 数组
     * @return maxValue
     */
    public static int maxValue(int... arr) {
        int max = arr[0];
        for (int temp : arr) {
            max = Math.max(temp, max);
        }
        return max;
    }

    /**
     * 找到数组中最小的数
     *
     * @param arr 数组
     * @return minValue
     */
    public static int minValue(int... arr) {
        int min = arr[0];
        for (int temp : arr) {
            min = Math.min(temp, min);
        }
        return min;
    }

}
