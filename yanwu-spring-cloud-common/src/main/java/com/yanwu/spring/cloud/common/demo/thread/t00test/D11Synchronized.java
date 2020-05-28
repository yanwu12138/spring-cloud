package com.yanwu.spring.cloud.common.demo.thread.t00test;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-05-28 22:13:02.
 * <p>
 * describe:
 * 写一个固定容量的同步容器，拥有put()、get()、getCount()函数
 * 能够支持2个生产者和10个消费者的阻塞调用
 */
public class D11Synchronized {
    private static final List VALUES = new ArrayList(1);
}
