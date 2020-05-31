package com.yanwu.spring.cloud.common.demo.d03thread.t05;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-05-08 22:26:24.
 * <p>
 * describe: 线程组
 */
@Slf4j
public class MyThreadGroup {

    public static void main(String[] args) {
        ThreadGroup group = new ThreadGroup("yanwu");
        Result result = new Result();
        Searcher searcher = new Searcher(result);
        for (int i = 0; i < 10; i++) {
            new Thread(group, searcher).start();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (Exception e) {
                log.error("group main error.", e);
            }
        }
        log.info("thread num: {}", group.activeCount());
        log.info("thread group list: ");
        group.list();
        Thread[] threads = new Thread[group.activeCount()];
        group.enumerate(threads);
        for (Thread thread : threads) {
            log.info("thread: name: {} {}", thread.getName(), thread.getState());
        }
        waitFinish(group);
        group.interrupt();
    }

    private static void waitFinish(ThreadGroup group) {
        while (group.activeCount() > 9) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (Exception e) {
                log.error("error.", e);
            }
        }
    }

}

@Slf4j
@NoArgsConstructor
@AllArgsConstructor
class Searcher implements Runnable {

    private Result result;

    @Override
    public void run() {
        String name = Thread.currentThread().getName();
        log.info("thread start: {}", name);
        try {
            doTask();
        } catch (Exception e) {
            log.error("thread exception: {}", name, e);
        }
        log.info("thread done: {}", name);
    }

    private void doTask() throws Exception {
        Random random = new Random(new Date().getTime());
        int value = random.nextInt();
        log.info("thread random: {} {}", Thread.currentThread().getName(), value);
        TimeUnit.SECONDS.sleep(value);
    }
}

@Data
class Result {
    private String name;
}