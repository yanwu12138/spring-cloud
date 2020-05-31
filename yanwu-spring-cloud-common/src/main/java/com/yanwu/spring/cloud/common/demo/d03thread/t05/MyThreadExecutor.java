package com.yanwu.spring.cloud.common.demo.d03thread.t05;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-05-08 22:17:56.
 * <p>
 * describe: 线程池
 */
@Slf4j
public class MyThreadExecutor {

    private static final ThreadPoolExecutor EXECUTOR;

    static {
        // ----- 默认线程池
        EXECUTOR = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        // -----
//        EXECUTOR = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    }

    private static void submit(Task task) {
        log.info("a new task has arrived");
        EXECUTOR.execute(task);
        log.info("pole size: {}", EXECUTOR.getPoolSize());
        log.info("active count: {}", EXECUTOR.getActiveCount());
        log.info("completed tasks: {}", EXECUTOR.getCompletedTaskCount());
    }

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 10; i++) {
            Task task = new Task().setTaskName("task—-" + i);
            TimeUnit.SECONDS.sleep(1);
            submit(task);
        }
        EXECUTOR.shutdown();
        log.info("--------------------");
    }

    @Data
    @Accessors(chain = true)
    private static class Task implements Runnable {
        private String taskName;

        @Override
        public void run() {
            try {
                int duration = RandomUtils.nextInt(0, 5);
                log.info("task: {}, doing a task during: {}", Thread.currentThread().getName(), duration);
                TimeUnit.SECONDS.sleep(duration);
            } catch (Exception e) {
                log.error("task sleep error.", e);
            }
        }
    }
}
