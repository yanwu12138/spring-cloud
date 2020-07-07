package com.yanwu.spring.cloud.common.demo.d07structure.s02stack;

import com.yanwu.spring.cloud.common.demo.d07structure.s00model.DoubleNode;

import java.io.Serializable;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-07-06 21:29:04.
 * <p>
 * describe: 双节点实现栈
 */
public class S03DoubleStack<E extends Serializable> implements S00Stack<DoubleNode<E>> {
    @Override
    public boolean push(DoubleNode<E> value) {
        return false;
    }

    @Override
    public DoubleNode<E> pop() {
        return null;
    }

    @Override
    public DoubleNode<E> top() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }
}
