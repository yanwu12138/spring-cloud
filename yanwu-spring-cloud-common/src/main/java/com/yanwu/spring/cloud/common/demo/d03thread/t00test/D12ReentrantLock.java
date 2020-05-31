package com.yanwu.spring.cloud.common.demo.d03thread.t00test;

import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-05-28 22:13:02.
 * <p>
 * describe:
 * 写一个固定容量的同步容器，拥有put()、get()、getCount()函数
 * 能够支持2个生产者和10个消费者的阻塞调用
 * <p>
 * 问题
 * A1
 * B2
 * C3
 * D4
 * ...
 * Z26
 */
@SuppressWarnings("all")
public class D12ReentrantLock {
    private static volatile int TOP = 0;
    private static final Integer DEFAULT_SIZE = 20;
    private static final Random RANDOM = new Random();
    private static final Integer[] VALUES = new Integer[DEFAULT_SIZE];

    private static final Lock LOCK = new ReentrantLock();
    private static final Condition CONSUMER = LOCK.newCondition();
    private static final Condition PRODUCER = LOCK.newCondition();

    public static void main(String[] args) {
        D12ReentrantLock sync = new D12ReentrantLock();
        for (int i = 0; i < 2; i++) {
            new Thread(sync::get, "consumer-" + i).start();
        }
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                sync.put(RANDOM.nextInt(10));
            }, "producer-" + i).start();
        }
    }

    private void put(Integer value) {
        try {
            LOCK.lock();
            while (true) {
                if (getCount() == DEFAULT_SIZE) {
                    PRODUCER.await();
                } else {
                    VALUES[TOP++] = value;
                    System.out.println(Thread.currentThread().getName() + " 生产 size: " + TOP + " value: " + value);
                    CONSUMER.signalAll();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            LOCK.unlock();
        }
    }

    private Integer get() {
        try {
            LOCK.lock();
            while (true) {
                if (getCount() == 0) {
                    CONSUMER.await();
                } else {
                    Integer result = VALUES[TOP - 1];
                    System.out.println(Thread.currentThread().getName() + " 消费 size: " + TOP + " value: " + result);
                    VALUES[--TOP] = null;
                    PRODUCER.signalAll();
                    return result;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            LOCK.unlock();
        }
        return null;
    }

    private int getCount() {
        return TOP;
    }
}
