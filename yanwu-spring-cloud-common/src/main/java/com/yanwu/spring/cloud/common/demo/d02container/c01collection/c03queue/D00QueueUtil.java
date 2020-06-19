package com.yanwu.spring.cloud.common.demo.d02container.c01collection.c03queue;

import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.Queue;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/6/19 11:40.
 * <p>
 * description:
 */
@Slf4j
class D00QueueUtil {

    protected static final Integer DEFAULT_SIZE = 10;
    private static final Integer DEFAULT_ITEM = 10;

    private static void add(Queue<Integer> queue) {
        for (int i = 0; i < DEFAULT_SIZE; i++) {
            queue.offer(i);
        }
    }

    protected static void test(Queue<Integer> queue) {
        log.info("---------- {} ----------", queue.getClass());
        add(queue);
        queue.offer(DEFAULT_ITEM);
        log.info("queue: {}", queue);
        log.info("poll: {}", queue.poll());
        log.info("peek: {}", queue.peek());

        Iterator<Integer> iterator = queue.iterator();
        while (iterator.hasNext()) {
            Integer next = iterator.next();
            log.info("next: {}", next);
            iterator.remove();
        }
    }

}
