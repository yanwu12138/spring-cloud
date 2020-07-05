package com.yanwu.spring.cloud.common.demo.d07structure;

import java.io.Serializable;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-07-05 22:30:08.
 * <p>
 * describe: 单链表
 */
public class S01Node<E extends Serializable> {
    private E value;
    private int size;
    private S01Node<E> next;

    public void insert(E value) {
    }

    public void select(E value) {
    }

    public void update(E oldVal, E newVal) {
    }

    public void delete(E value) {
    }
}
