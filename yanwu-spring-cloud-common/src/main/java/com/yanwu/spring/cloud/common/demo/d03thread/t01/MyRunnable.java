package com.yanwu.spring.cloud.common.demo.d03thread.t01;

import lombok.extern.slf4j.Slf4j;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-04-23 21:26:44.
 * <p>
 * describe: 实现多线程的方式一：实现runnable接口，重写run函数
 */
@Slf4j
public class MyRunnable implements Runnable{
    @Override
    public void run() {
        log.info("run thread name: {}", Thread.currentThread().getName());
    }
    public static void main(String[] args) {
        Thread thread = new Thread(new MyRunnable());
        thread.start();
        log.info("main thread name: {}", Thread.currentThread().getName());
    }
}
