package com.yanwu.spring.cloud.common.demo.d03thread.t06sync;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/28 13:55.
 * <p>
 * description:
 */
@SuppressWarnings("all")
public class D007Synchronized implements Runnable {

    private static int count = 10;

    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            D007Synchronized sync = new D007Synchronized();
            new Thread(sync).start();
        }
    }

    @Override
    public synchronized void run() {
        count--;
        System.out.println(Thread.currentThread().getName() + " count = " + count);
    }

}
