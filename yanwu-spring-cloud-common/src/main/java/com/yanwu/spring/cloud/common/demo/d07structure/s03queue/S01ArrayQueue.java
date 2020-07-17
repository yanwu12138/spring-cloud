package com.yanwu.spring.cloud.common.demo.d07structure.s03queue;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-07-07 21:07:10.
 * <p>
 * describe:
 */
@Data
@Slf4j
public class S01ArrayQueue<E extends Serializable> {
    private int top;
    private int num;
    private int left;
    private int right;
    private final int size;
    private final E[] values;

    public S01ArrayQueue(int size) {
        this.top = -1;
        this.num = this.left = this.right = 0;
        this.size = size;
        this.values = (E[]) new Serializable[size];
    }

    public boolean leftEnqueue(E value) {
        if (value == null || size == size()) {
            return false;
        }

        return true;
    }

    public E leftDequeue() {
        return dequeue(true);
    }

    public boolean rightEnqueue(E value) {
        if (value == null || size == size()) {
            return false;
        }
        return true;
    }

    public E rightDequeue() {
        return dequeue(false);
    }

    public int size() {

        return top + 1;
    }

    private int getIndex(boolean lOrR) {
        return 0;
    }

    private E dequeue(boolean lOrR) {
        top--;
        num--;
        return null;
    }

    public static void main(String[] args) {
        S01ArrayQueue<Integer> queue = new S01ArrayQueue<>(5);
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
