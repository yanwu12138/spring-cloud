package com.yanwu.spring.cloud.common.demo.d03thread.t09pool.executor;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-05-31 23:54:43.
 * <p>
 * describe:
 */
public class D11ForkJoin {
    public static final Integer[] NUM = new Integer[1000000];
    public static final Integer MAX_NUM = 5000;
    public static final Random RANDOM = new Random();

    static {
        for (int i = 0; i < NUM.length; i++) {
            NUM[i] = RANDOM.nextInt(MAX_NUM);
        }
    }

    public static void main(String[] args) throws Exception {
        long start1 = System.currentTimeMillis();
        ForkJoinPool forkJoin1 = new ForkJoinPool();
        NumAction action = new NumAction(0, NUM.length);
        forkJoin1.execute(action);
        System.out.println("NumAction time: " + (System.currentTimeMillis() - start1));
        System.out.println("==========================================");

        long start2 = System.currentTimeMillis();
        ForkJoinPool forkJoin2 = new ForkJoinPool();
        NumTask task = new NumTask(0, NUM.length);
        forkJoin2.execute(task);
        Long result2 = task.join();
        System.out.println("NumTask " + " sum: " + result2 + " time: " + (System.currentTimeMillis() - start2));

        System.in.read();
    }

    @NoArgsConstructor
    @AllArgsConstructor
    private static class NumAction extends RecursiveAction {
        private static final long serialVersionUID = -5235423911194235679L;
        private Integer begin;
        private Integer end;

        @Override
        protected void compute() {
            if (end - begin <= MAX_NUM) {
                long sum = 0L;
                for (int i = begin; i < end; i++) {
                    sum += NUM[i];
                }
                System.out.println("from:" + begin + " to:" + end + " = " + sum);
            } else {
                int middle = begin + (end - begin) / 2;
                NumAction subTask1 = new NumAction(begin, middle);
                NumAction subTask2 = new NumAction(middle, end);
                subTask1.fork();
                subTask2.fork();
            }
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    static class NumTask extends RecursiveTask<Long> {
        private static final long serialVersionUID = 8933223305881698463L;
        private Integer begin;
        private Integer end;

        @Override
        protected Long compute() {
            if (end - begin <= MAX_NUM) {
                long sum = 0L;
                for (int i = begin; i < end; i++) {
                    sum += NUM[i];
                }
                return sum;
            }
            int middle = begin + (end - begin) / 2;
            NumTask subTask1 = new NumTask(begin, middle);
            NumTask subTask2 = new NumTask(middle, end);
            subTask1.fork();
            subTask2.fork();
            return subTask1.join() + subTask2.join();
        }
    }
}
