package com.yanwu.spring.cloud.common.demo.d02container.c01collection.c03queue;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/6/18 14:01.
 * <p>
 * description:
 */
public class D10DelayQueue {

    public static void main(String[] args) {
        DelayQueue<Delayed> queue = new DelayQueue<>();
    }
}
