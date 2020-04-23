package com.yanwu.spring.cloud.common.demo.thread.t03;

import lombok.extern.slf4j.Slf4j;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-04-23 22:25:59.
 * <p>
 * describe: 资源不安全问题的解决方法一：使用volatile关键字修饰共享资源（不安全）
 */
@Slf4j
public class VolatileThread implements Runnable {
    private volatile Boolean flag = true;

    @Override
    public void run() {
        int i = 0;
        while (flag) {
            i++;
        }
        log.info("run thread : {} done. i: {}", Thread.currentThread().getName(), i);
    }

    public static void main(String[] args) {
        VolatileThread thread = new VolatileThread();
        new Thread(thread).start();
        thread.flag = false;
        log.info("main done.");
    }
}
