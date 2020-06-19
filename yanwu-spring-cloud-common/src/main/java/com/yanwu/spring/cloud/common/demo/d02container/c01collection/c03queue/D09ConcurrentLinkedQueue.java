package com.yanwu.spring.cloud.common.demo.d02container.c01collection.c03queue;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/6/18 14:01.
 * <p>
 * description:
 */
public class D09ConcurrentLinkedQueue {

    public static void main(String[] args) {
        D00QueueUtil.test(new ConcurrentLinkedQueue<>());
    }
}
