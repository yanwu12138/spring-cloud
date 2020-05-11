package com.yanwu.spring.cloud.common.demo.thread.t01;

import lombok.extern.slf4j.Slf4j;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020/04/23
 * <p>
 * describe: 实现多线程的方式二：继承Thread类，重写run函数（不推荐，占用继承位置）
 */
@Slf4j
public class MyThread extends Thread {
    @Override
    public void run() {
        super.run();
        log.info("run thread name: {}", Thread.currentThread().getName());
    }
    public static void main(String[] args) {
        MyThread thread = new MyThread();
        thread.start();
        log.info("main thread name: {}", Thread.currentThread().getName());
    }
}