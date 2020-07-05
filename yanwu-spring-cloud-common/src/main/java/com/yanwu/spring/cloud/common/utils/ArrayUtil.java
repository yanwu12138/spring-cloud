package com.yanwu.spring.cloud.common.utils;

/**
 * @author XuBaofeng.
 * @date 2018-09-28 17:07.
 * <p>
 * description: 数组工具类
 */
public class ArrayUtil {
    /**
     * 当数组为null或者数组中是否所有的元素全都为null时返回true, 否则返回false
     *
     * @param source
     * @return
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
    public static boolean isEquals(int[] arrA, int[] arrB) {
        if (arrA == null || arrB == null || arrA.length != arrB.length) {
            return false;
        }
        if (arrA == arrB) {
            return true;
        }
        for (int i = 0; i < arrA.length; i++) {
            if (arrA[i] != arrB[i]) {
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
}
