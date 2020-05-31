package com.yanwu.spring.cloud.common.demo.d03thread.t03;

import java.util.concurrent.TimeUnit;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/26 9:45.
 * <p>
 * description: 同步方法
 */
public class SynchronizedFunc implements Runnable{
    private Integer count = 100;

    public static void main(String[] args) throws InterruptedException {
        SynchronizedFunc func = new SynchronizedFunc();
        for (int i = 0; i < 100; i++) {
            new Thread(func).start();
        }
        TimeUnit.SECONDS.sleep(2);
        System.out.println(func.count);
    }


    @Override
    public void run() {
        count--;
    }
}
