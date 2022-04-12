package com.yanwu.spring.cloud.common.demo.d07structure.s01list;

import com.yanwu.spring.cloud.common.demo.d07structure.s00model.Node;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-07-05 22:30:08.
 * <p>
 * describe: 单链表
 */
@Slf4j
public class S02LinkedList<E extends Serializable> implements S00List<Node<Integer>> {
    /*** 链表的元素个数 ***/
    private Integer size;
    /*** 链表的头节点 ***/
    private Node<Integer> head;
    /*** 链表的尾节点 ***/
    private Node<Integer> tail;

    @Override
    public boolean add(Node<Integer> value) {
        return false;
    }

    @Override
    public Node<Integer> get(int index) {
        return null;
    }

    @Override
    public Node<Integer> set(int index, Node<Integer> newVal) {
        return null;
    }

    @Override
    public int set(Node<Integer> oldVal, Node<Integer> newVal) {
        return 0;
    }

    @Override
    public int del(Node<Integer> value) {
        return 0;
    }

    @Override
    public Node<Integer> del(int index) {
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