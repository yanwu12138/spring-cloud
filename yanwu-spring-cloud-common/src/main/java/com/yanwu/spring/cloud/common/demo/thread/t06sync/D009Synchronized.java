package com.yanwu.spring.cloud.common.demo.thread.t06sync;

import java.util.concurrent.TimeUnit;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/28 13:55.
 * <p>
 * description: synchronized使用的锁对象应该避免更改锁对象的引用，不然会导致锁不住的情况
 */
@SuppressWarnings("all")
public class D009Synchronized {

    Object lock = new Object();

    public static void main(String[] args) {
        D009Synchronized sync = new D009Synchronized();
        new Thread(sync::test1).start();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sync.lock = new Object();
        new Thread(sync::test1).start();
    }

    public void test1() {
        synchronized (lock) {
            System.out.printf("%s start %n", Thread.currentThread().getName());
            while (true) {
            }
        }
    }


}
