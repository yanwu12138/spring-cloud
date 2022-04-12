package com.yanwu.spring.cloud.common.demo.d07structure.s03queue;

import com.yanwu.spring.cloud.common.demo.d07structure.s00model.Node;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-07-06 21:29:29.
 * <p>
 * describe:
 */
@Slf4j
public class S03DoubleQueue<E extends Serializable> {

    private int top;
    private final int size;
    private Node<E> left;
    private Node<E> right;

    public S03DoubleQueue(int size) {
        this.top = -1;
        this.size = size;
        this.left = null;
        this.right = null;
    }

    public boolean leftEnqueue(E value) {
        return enqueue(value, true);
    }

    public E leftDequeue() {
        return dequeue(true);
    }

    public boolean rightEnqueue(E value) {
        return enqueue(value, false);
    }

    public E rightDequeue() {
        return dequeue(false);
    }

    public int size() {
        return top + 1;
    }

    private boolean enqueue(E value, boolean lOrR) {
        if (value == null || size() == size) {
            return false;
        }
        Node<E> newNode = new Node<>(value);
        if (left == null) {
            left = newNode;
            right = newNode;
        } else {
            if (lOrR) {
                newNode.setNext(left);
                left.setLast(newNode);
                left = newNode;
            } else {
                right.setNext(newNode);
                newNode.setLast(right);
                right = newNode;
            }
        }
        top++;
        return true;
    }

    private E dequeue(boolean lOrR) {
        if (size() == 0) {
            return null;
        }
        Node<E> node;
        if (lOrR) {
            node = left;
            left = left.getNext();
            if (left != null) {
                left.setLast(null);
            }
        } else {
            node = right;
            right = right.getLast();
            if (right != null) {
                right.setNext(null);
            }
        }
        top--;
        if (size() == 0) {
            left = null;
            right = null;
        }
        return node.getValue();
    }


    public static void main(String[] args) {
        S03DoubleQueue<Integer> queue = new S03DoubleQueue<>(5);
        log.info("queue leftEnqueue: 1 >> {}", queue.leftEnqueue(1));
        log.info("queue leftEnqueue: 2 >> {}", queue.leftEnqueue(2));
        log.info("queue leftEnqueue: 3 >> {}", queue.leftEnqueue(3));

        log.info("queue rightEnqueue: 4 >> {}", queue.rightEnqueue(4));
        log.info("queue rightEnqueue: 5 >> {}", queue.rightEnqueue(5));
        log.info("queue rightEnqueue: 6 >> {}", queue.rightEnqueue(6));

        log.info("queue: {}", queue);

        log.info("queue leftDequeue: {}", queue.leftDequeue());
        log.info("queue leftDequeue: {}", queue.leftDequeue());
        log.info("queue leftDequeue: {}", queue.leftDequeue());

        log.info("queue rightDequeue: {}", queue.rightDequeue());
        log.info("queue rightDequeue: {}", queue.rightDequeue());
        log.info("queue rightDequeue: {}", queue.rightDequeue());

        log.info("queue: {}", queue);

        log.info("queue leftEnqueue: 1 >> {}", queue.leftEnqueue(1));
        log.info("queue leftEnqueue: 2 >> {}", queue.leftEnqueue(2));
        log.info("queue leftEnqueue: 3 >> {}", queue.leftEnqueue(3));
        log.info("queue leftEnqueue: 4 >> {}", queue.leftEnqueue(4));
        log.info("queue leftEnqueue: 5 >> {}", queue.leftEnqueue(5));
        log.info("queue leftEnqueue: 6 >> {}", queue.leftEnqueue(6));

        log.info("queue: {}", queue);

        log.info("queue rightDequeue: {}", queue.rightDequeue());
        log.info("queue rightDequeue: {}", queue.rightDequeue());
        log.info("queue rightDequeue: {}", queue.rightDequeue());
        log.info("queue rightDequeue: {}", queue.rightDequeue());
        log.info("queue rightDequeue: {}", queue.rightDequeue());
        log.info("queue rightDequeue: {}", queue.rightDequeue());

        log.info("queue: {}", queue);

        log.info("queue rightEnqueue: 1 >> {}", queue.rightEnqueue(1));
        log.info("queue rightEnqueue: 2 >> {}", queue.rightEnqueue(2));
        log.info("queue rightEnqueue: 3 >> {}", queue.rightEnqueue(3));
        log.info("queue rightEnqueue: 4 >> {}", queue.rightEnqueue(4));
        log.info("queue rightEnqueue: 5 >> {}", queue.rightEnqueue(5));
        log.info("queue rightEnqueue: 6 >> {}", queue.rightEnqueue(6));

        log.info("queue: {}", queue);

        log.info("queue leftDequeue: {}", queue.leftDequeue());
        log.info("queue leftDequeue: {}", queue.leftDequeue());
        log.info("queue leftDequeue: {}", queue.leftDequeue());
        log.info("queue leftDequeue: {}", queue.leftDequeue());
        log.info("queue leftDequeue: {}", queue.leftDequeue());
        log.info("queue leftDequeue: {}", queue.leftDequeue());
    }
}
