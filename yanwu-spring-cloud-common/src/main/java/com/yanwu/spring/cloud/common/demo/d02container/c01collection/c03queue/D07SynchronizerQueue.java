package com.yanwu.spring.cloud.common.demo.d02container.c01collection.c03queue;

import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/6/18 14:00.
 * <p>
 * description:
 */
public class D07SynchronizerQueue {

    public static void main(String[] args) {
        D00QueueUtil.test(new SynchronousQueue<>());
    }
}
