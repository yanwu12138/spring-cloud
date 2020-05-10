package com.yanwu.spring.cloud.common.demo.thread.t05;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-05-10 21:33:11.
 * <p>
 * describe: callable示例，通过10个线程计算1～1000的总和
 */
@Data
@Slf4j
@Accessors(chain = true)
public class MyThreadCallable implements Callable<Integer> {
    private int startNum;
    private int endNum;

    public static void main(String[] args) {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
        List<Future<Integer>> result = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            MyThreadCallable task = new MyThreadCallable().setStartNum(i * 100 + 1).setEndNum((i + 1) * 100);
            Future<Integer> future = executor.submit(task);
            result.add(future);
        }
        do {
            log.info("main 已经完成的任务数: {}", executor.getCompletedTaskCount());
            for (int i = 0; i < result.size(); i++) {
                Future<Integer> future = result.get(i);
                log.info("main task id: {}, done: {}", i, future.isDone());
            }
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (Exception e) {
                log.error("sleep error.", e);
            }
        } while (executor.getCompletedTaskCount() < result.size());
        int total = 0;
        for (Future<Integer> future : result) {
            try {
                total += future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        log.info("total: {}", total);
        executor.shutdown();
    }

    @Override
    public Integer call() throws Exception {
        int sum = 0;
        while (startNum <= endNum) {
            sum += startNum;
            startNum++;
        }
        TimeUnit.MILLISECONDS.sleep(RandomUtils.nextInt(0, 1000));
        log.info("task: {}, sum: {}", Thread.currentThread().getName(), sum);
        return sum;
    }
}
