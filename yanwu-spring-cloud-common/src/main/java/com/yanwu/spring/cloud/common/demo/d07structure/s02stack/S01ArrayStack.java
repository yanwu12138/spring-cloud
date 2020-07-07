package com.yanwu.spring.cloud.common.demo.d07structure.s02stack;

import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-07-07 21:06:21.
 * <p>
 * describe: 数组实现栈
 */
@Slf4j
public class S01ArrayStack<E extends Serializable> implements S00Stack<E> {
    private int top;
    private final int size;
    private final E[] stack;

    public S01ArrayStack(int size) {
        this.top = -1;
        this.size = size;
        this.stack = (E[]) new Serializable[size];
    }

    @Override
    public boolean push(E value) {
        if (value == null || top == (size - 1)) {
            return false;
        }
        stack[++top] = value;
        return true;
    }

    @Override
    public E pop() {
        if (top < 0) {
            return null;
        }
        E result = stack[top];
        stack[top--] = null;
        return result;
    }

    @Override
    public E top() {
        return top >= 0 ? stack[top] : null;
    }

    @Override
    public int size() {
        return top + 1;
    }

    public static void main(String[] args) {
        S01ArrayStack<Integer> myStack = new S01ArrayStack<>(10);
        log.info("push: {}", myStack.push(0));
        log.info("push: {}", myStack.push(1));
        log.info("push: {}", myStack.push(2));
        log.info("push: {}", myStack.push(3));
        log.info("push: {}", myStack.push(4));

        log.info("pop: {}", myStack.pop());
        log.info("top: {}", myStack.top());

        log.info("push: {}", myStack.push(5));
        log.info("push: {}", myStack.push(6));
        log.info("push: {}", myStack.push(7));
        log.info("push: {}", myStack.push(8));
        log.info("push: {}", myStack.push(9));
        log.info("push: {}", myStack.push(10));
        log.info("push: {}", myStack.push(11));

        log.info("size: {}", myStack.size());

        log.info("pop: {}", myStack.pop());
        log.info("pop: {}", myStack.pop());
        log.info("pop: {}", myStack.pop());
        log.info("pop: {}", myStack.pop());
        log.info("pop: {}", myStack.pop());
        log.info("pop: {}", myStack.pop());
        log.info("pop: {}", myStack.pop());
        log.info("pop: {}", myStack.pop());
        log.info("pop: {}", myStack.pop());
        log.info("pop: {}", myStack.pop());
        log.info("pop: {}", myStack.pop());

        log.info("push: {}", myStack.push(5));
        log.info("push: {}", myStack.push(6));
        log.info("pop: {}", myStack.pop());
        log.info("top: {}", myStack.top());

        log.info("size: {}", myStack.size());
    }
}
