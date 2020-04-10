package com.yanwu.spring.cloud.common.utils;

import java.util.*;

public class ListUtil {
    /**
     * 求ls对ls2的差集,即ls中有，但ls2中没有的
     *
     * @param ls
     * @param ls2
     * @return
     */
    public static <T> List<T> diff(List<T> ls, List<T> ls2) {
        List<T> list = new ArrayList(Arrays.asList(new Object[ls.size()]));
        Collections.copy(list, ls);
        list.removeAll(ls2);
        return list;
    }

    /**
     * 求2个集合的交集
     *
     * @param ls
     * @param ls2
     * @return
     */
    public static <T> List<T> intersect(List<T> ls, List<T> ls2) {
        List<T> list = new ArrayList(Arrays.asList(new Object[ls.size()]));
        Collections.copy(list, ls);
        list.retainAll(ls2);
        return list;
    }

    /**
     * 求2个集合的并集
     *
     * @param ls
     * @param ls2
     * @return
     */
    public static <T> List<T> union(List<T> ls, List<T> ls2) {
        List<T> list = new ArrayList(Arrays.asList(new Object[ls.size()]));
        //将ls的值拷贝一份到list中
        Collections.copy(list, ls);
        list.removeAll(ls2);
        list.addAll(ls2);
        return list;
    }

    /**
     * 判断List集合是否不为空
     *
     * @param objList List集合
     * @return true:有元素，false:空
     */
    public static <T> boolean isNotEmpty(List<T> objList) {
        removeEmpty(objList);
        return objList != null && objList.size() != 0;
    }

    /**
     * 集合过滤, 过滤掉List集合中所有为null的元素
     *
     * @param source
     * @param <T>
     */
    public static <T> void removeEmpty(List<T> source) {
        if (source == null || source.size() == 0) {
            return;
        }
        source.removeIf(Objects::isNull);
    }

}
