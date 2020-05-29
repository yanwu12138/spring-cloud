package com.yanwu.spring.cloud.common.demo.thread.t00test;

import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-05-28 22:13:02.
 * <p>
 * describe:
 * 写一个固定容量的同步容器，拥有put()、get()、getCount()函数
 * 能够支持2个生产者和10个消费者的阻塞调用
 */
@SuppressWarnings("all")
public class D11WaitNotify {
    private static Integer INDEX = 0;
    private static final Integer DEFAULT_SIZE = 20;
    private static final Random RANDOM = new Random();
    private static final Integer[] VALUES = new Integer[DEFAULT_SIZE];

    private static final ReentrantLock LOCK = new ReentrantLock();
    private static final Condition CONSUMER = LOCK.newCondition();
    private static final Condition PRODUCER = LOCK.newCondition();

    public static void main(String[] args) {
        D11WaitNotify sync = new D11WaitNotify();
        for (int n = 0; n < 2; n++) {
            new Thread(() -> {
                for (int m = 0; m < 50; m++) {
                    Integer integer = sync.get();
                }
            }, "consumer-" + n).start();
        }
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                for (int j = 0; j < 10; j++) {
                    sync.put(RANDOM.nextInt(10));
                }
            }, "producer-" + i).start();
        }
    }

    public void put(Integer value) {
        LOCK.lock();
        try {
            while (getCount().compareTo(DEFAULT_SIZE) >= 0) {
                PRODUCER.await();
            }
            VALUES[INDEX++] = value;
            System.out.println(Thread.currentThread().getName() + " 生产了: " + value + " 个数：" + getCount());
            CONSUMER.signalAll();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            LOCK.unlock();
        }
    }

    private Integer get() {
        Integer result = null;
        LOCK.lock();
        try {
            while (getCount().compareTo(0) <= 0) {
                CONSUMER.await();
            }
            result = VALUES[--INDEX];
            System.out.println(Thread.currentThread().getName() + " 消费了: " + result + " 个数：" + getCount());
            PRODUCER.signalAll();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            LOCK.unlock();
        }
        return result;
    }

    private Integer getCount() {
        return INDEX;
    }
}
