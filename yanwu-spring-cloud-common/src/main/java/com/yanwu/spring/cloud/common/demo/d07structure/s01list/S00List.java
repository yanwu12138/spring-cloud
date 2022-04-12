package com.yanwu.spring.cloud.common.demo.d07structure.s01list;

import java.io.Serializable;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-07-07 23:00:38.
 * <p>
 * describe:
 */
public interface S00List<E extends Serializable> {

    /***
     * 添加元素
     * @param value 元素
     * @return [true: 操作成功; false: 操作失败]
     */
    boolean add(E value);

    /***
     * 根据角标获取元素
     * @param index 角标
     * @return 元素
     */
    E get(int index);

    /***
     * 根据角标设置元素的新值
     * @param index 角标
     * @param newVal 新值
     * @return 旧值
     */
    E set(int index, E newVal);

    /***
     * 根据旧值设置新值
     * @param oldVal 旧值
     * @param newVal 新值
     * @return 更改元素的个数
     */
    int set(E oldVal, E newVal);

    /***
     * 根据值删除元素
     * @param value 值
     * @return 删除元素的个数
     */
    int del(E value);

    /***
     * 根据角标删除元素
     * @param index 角标
     * @return 被删除的元素
     */
    E del(int index);

    /***
     * 翻转数组
     */
    void reverse();

    /**
     * 获取数组中元素的个数
     */
    int size();
}
