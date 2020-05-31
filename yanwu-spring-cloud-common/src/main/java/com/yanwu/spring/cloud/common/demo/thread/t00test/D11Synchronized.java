package com.yanwu.spring.cloud.common.demo.thread.t00test;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-05-28 22:13:02.
 * <p>
 * describe:
 * 写一个固定容量的同步容器，拥有put()、get()、getCount()函数
 * 能够支持2个生产者和10个消费者的阻塞调用
 */
public class D11Synchronized {
    private static final Integer DEFAULT_SIZE = 20;
    private static final Object[] VALUES = new Object[DEFAULT_SIZE];
    private static volatile int TOP = 0;

    public static void main(String[] args) {
        D11Synchronized sync = new D11Synchronized();
        for (int i = 0; i < 2; i++) {
            new Thread(sync::get, "consumer-" + i).start();
        }
        for (int i = 0; i < 10; i++) {
            new Thread(sync::put, "producer-" + i).start();
        }
    }

    private synchronized void put() {
        while (true) {
            if (getCount() == DEFAULT_SIZE) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                VALUES[TOP++] = new Object();
                System.out.println(Thread.currentThread().getName() + " 生产 top: " + TOP + " value: " + VALUES[TOP - 1]);
                this.notifyAll();
            }
        }
    }

    private synchronized void get() {
        while (true) {
            if (getCount() == 0) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println(Thread.currentThread().getName() + " 消费 top: " + TOP + " value: " + VALUES[TOP - 1]);
                VALUES[--TOP] = null;
                this.notifyAll();
            }
        }
    }

    private synchronized int getCount() {
        return TOP;
    }
}
