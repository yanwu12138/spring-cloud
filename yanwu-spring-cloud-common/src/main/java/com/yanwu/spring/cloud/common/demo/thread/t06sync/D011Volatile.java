package com.yanwu.spring.cloud.common.demo.thread.t06sync;

import java.util.concurrent.TimeUnit;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/28 11:56.
 * <p>
 * description:
 */
@SuppressWarnings("all")
public class D011Volatile {
    /**
     * volatile：通过volatile关键字保证变量在线程之间的可见性
     * 可见性通过缓存一致性保证
     * 缓存一致性通过缓存一致性协议与内存屏障来保证
     */
    private volatile Boolean flag = true;

    public static void main(String[] args) {
        D011Volatile s = new D011Volatile();
        new Thread(s::test, "volatile").start();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        s.flag = false;
    }

    private void test() {
        System.out.println("test start");
        while (flag) {
            System.out.println("test running");
        }
        System.out.println("test end");
    }

}
