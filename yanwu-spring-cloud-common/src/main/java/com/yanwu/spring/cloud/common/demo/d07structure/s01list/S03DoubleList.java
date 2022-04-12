package com.yanwu.spring.cloud.common.demo.d07structure.s01list;


import com.yanwu.spring.cloud.common.demo.d07structure.s00model.Node;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-07-06 21:27:24.
 * <p>
 * describe: 双链表
 */
@Slf4j
public class S03DoubleList<E extends Serializable> implements S00List {
    /*** 链表的元素个数 ***/
    private Integer size;
    /*** 链表的头节点 ***/
    private Node<E> head;
    /*** 链表的尾节点 ***/
    private Node<E> tail;

    public S03DoubleList() {
        this.size = 0;
    }

    @Override
    public boolean add(Serializable value) {
        return false;
    }

    @Override
    public Serializable get(int index) {
        return null;
    }

    @Override
    public Serializable set(int index, Serializable newVal) {
        return null;
    }

    @Override
    public int set(Serializable oldVal, Serializable newVal) {
        return 0;
    }

    @Override
    public int del(Serializable value) {
        return 0;
    }

    @Override
    public Serializable del(int index) {
        return null;
    }

    @Override
    public void reverse() {

    }

    @Override
    public int size() {
        return 0;
    }
}
