package com.yanwu.spring.cloud.common.core.utils;

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
}
