package com.yanwu.spring.cloud.common.demo.d07structure.s02stack;

import com.yanwu.spring.cloud.common.demo.d07structure.s00model.Node;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-07-06 21:28:33.
 * <p>
 * describe: 单节点实现栈
 */
@Slf4j
public class S02LinkedStack<E extends Serializable> implements S00Stack<Node<Integer>> {
    private int top;
    private final int size;
    private Node<Integer> head;
    private Node<Integer> tail;

    public S02LinkedStack(int size) {
        this.top = -1;
        this.size = size;
        this.head = this.tail = null;
    }

    @Override
    public boolean push(Node<Integer> node) {
        if (node == null || top == (size - 1)) {
            return false;
        }
        if (head == null) {
            head = node;
        } else {
            tail.setNext(node);
        }
        tail = node;
        top++;
        return true;
    }

    @Override
    public Node<Integer> pop() {
        if (top < 0) {
            return null;
        }
        Node<Integer> tail = this.head, result = this.tail;
        while (tail.getNext() != null) {
            if (result.equals(tail.getNext())) {
                break;
            }
            tail = tail.getNext();
        }
        top--;
        this.tail = tail;
        this.tail.setNext(null);
        return result;
    }

    @Override
    public Node<Integer> top() {
        return this.tail;
    }

    @Override
    public int size() {
        return top + 1;
    }

    public static void main(String[] args) {
        S02LinkedStack<Node<Integer>> myStack = new S02LinkedStack<>(10);
        log.info("push: {}", myStack.push(new Node<>(0)));
        log.info("push: {}", myStack.push(new Node<>(1)));
        log.info("push: {}", myStack.push(new Node<>(2)));
        log.info("push: {}", myStack.push(new Node<>(3)));
        log.info("push: {}", myStack.push(new Node<>(4)));

        log.info("pop: {}", myStack.pop());
        log.info("top: {}", myStack.top());

        log.info("push: {}", myStack.push(new Node<>(5)));
        log.info("push: {}", myStack.push(new Node<>(6)));
        log.info("push: {}", myStack.push(new Node<>(7)));
        log.info("push: {}", myStack.push(new Node<>(8)));
        log.info("push: {}", myStack.push(new Node<>(9)));
        log.info("push: {}", myStack.push(new Node<>(10)));
        log.info("push: {}", myStack.push(new Node<>(11)));

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

        log.info("push: {}", myStack.push(new Node<>(5)));
        log.info("push: {}", myStack.push(new Node<>(6)));
        log.info("pop: {}", myStack.pop());
        log.info("top: {}", myStack.top());

        log.info("size: {}", myStack.size());
    }
}
