package com.yanwu.spring.cloud.common.demo.d02container.c01collection.c03queue;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/6/18 13:59.
 * <p>
 * description:
 */
public class D05LinkedBlockingQueue {

    public static void main(String[] args) {
        D00QueueUtil.test(new LinkedBlockingQueue<>());
    }
}
