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
public class S01ArrayS00Stack<E extends Serializable> implements S00Stack<E> {
    private int top;
    private final int size;
    private final E[] stack;

    public S01ArrayS00Stack(int size) {
        this.top = -1;
        this.size = size;
        this.stack = (E[]) new Serializable[size];
    }

    @Override
    public void push(E value) {
        if (value == null || top == (size - 1)) {
            return;
        }
        stack[++top] = value;
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
        S01ArrayS00Stack<Integer> myStack = new S01ArrayS00Stack<>(10);
        myStack.push(0);
        myStack.push(1);
        myStack.push(2);
        myStack.push(3);
        myStack.push(4);

        log.info("pop: {}", myStack.pop());
        log.info("top: {}", myStack.top());

        myStack.push(5);
        myStack.push(6);
        myStack.push(7);
        myStack.push(8);
        myStack.push(9);

        myStack.push(10);

        log.info("stack size: {}", myStack.size());

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


        myStack.push(5);
        myStack.push(6);
        log.info("pop: {}", myStack.pop());
        log.info("top: {}", myStack.top());
    }
}
