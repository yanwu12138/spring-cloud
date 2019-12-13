package com.yanwu.spring.cloud.common.demo;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2019/12/02
 * <p>
 * describe:
 */
public class MyStack01<E> {

    private static final int DEFAULT_SIZE = 10;

    private E[] data;
    private int size;
    private int top = -1;

    MyStack01() {
        new MyStack01<>(DEFAULT_SIZE);
    }

    MyStack01(int size) {
        this.size = size;
        data = (E[]) new Object[size];
    }

    /**
     * 判断栈是否为控
     *
     * @return
     */
    public boolean isEmpty() {
        return top == -1;
    }

    public boolean push(E e) {
        if (top == size - 1) {
            return false;
        }
        data[++top] = e;
        return true;
    }

    public E pop() {
        if (isEmpty()) {
            return null;
        }
        E result = data[top];
        data[top--] = null;
        return result;
    }

    public E top() {
        if (isEmpty()) {
            return null;
        }
        return data[top];
    }

    public static void main(String[] args) {
        MyStack01<String> stack = new MyStack01<>(DEFAULT_SIZE);
        for (int i = 0; i < DEFAULT_SIZE + 1; i++) {
            System.out.println("push: " + stack.push(String.valueOf(i)) + ", item: " + i);
        }
        for (int i = 0; i < DEFAULT_SIZE + 1; i++) {
            System.out.println("top: " + stack.top() + ", pop: " + stack.pop());
        }
    }

}
