package com.yanwu.spring.cloud.common.demo.java.j01;

import java.util.PriorityQueue;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-05-11 22:05:26.
 * <p>
 * describe: priorityQueue是一个基于优先级堆的无界队列。
 * @see PriorityQueue
 */
public class MyPriorityQueue {

    private static final PriorityQueue<Integer> PRIORITY_QUEUE;

    static {
        PRIORITY_QUEUE = new PriorityQueue<>();
    }

    public static void main(String[] args) {
        // ----- 向队列中插入元素[add: 当插入失败时抛出异常; offer: 当插入失败时返回false]
        PRIORITY_QUEUE.add(9);
        PRIORITY_QUEUE.offer(10);
    }

}
