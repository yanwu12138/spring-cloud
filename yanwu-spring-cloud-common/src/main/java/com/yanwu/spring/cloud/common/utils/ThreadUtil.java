package com.yanwu.spring.cloud.common.utils;

import com.yanwu.spring.cloud.common.pojo.Result;
import com.yanwu.spring.cloud.common.pojo.ThreadInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

/**
 * @author Baofeng Xu
 * @date 2021/4/26 14:39.
 * <p>
 * description:
 */
@Slf4j
@SuppressWarnings("unused")
public class ThreadUtil {

    private static final Map<String, ThreadInfo> THREAD_CACHE = new ConcurrentHashMap<>();
    private static final AtomicLong THREAD_TASK_ID = new AtomicLong(1);

    private ThreadUtil() {
        throw new UnsupportedOperationException("ThreadUtil should never be instantiated");
    }

    public static void main(String[] args) throws Exception {
        System.out.println("==================================================");
        Result<String> result = asyncExec(() -> {
            log.info("function exec 1111");
            return Result.success("function callable result. " + SystemUtil.getSystemType());
        }, param -> {
            log.info("function exec 2222, param: {}", param);
            return Result.success("function func result.");
        });
        log.info("function exec 3333, result: {}", result);


        System.out.println("==================================================");
        // ----- 等待线程
        ThreadInfo instance = ThreadInfo.getInstance(ThreadUtil.getUniId(), 3000L);
        AtomicBoolean isWait = new AtomicBoolean(false);

        Thread testThread1 = new Thread(() -> {
            ThreadUtil.sleep(1000L);
            for (int i = 0; i < 200; i++) {
                if (isWait.get()) {
                    ThreadUtil.threadNotify(instance.getKey(), "线程唤醒");
                    break;
                }
                ThreadUtil.sleep(10L);
            }
        });
        testThread1.setName("test1");
        testThread1.start();

        Result<?> waitResult = ThreadUtil.threadWait(instance, isWait);
        log.info("instance result: {}", waitResult);
    }

    /**
     * 获取一个唯一的自增ID
     */
    public static String getUniId() {
        return String.valueOf(THREAD_TASK_ID.getAndIncrement());
    }

    /**
     * 线程等待
     */
    public static Result<?> threadWait(ThreadInfo threadInfo, AtomicBoolean isWait) {
        String key = threadInfo.getKey();
        Long timeout = threadInfo.getTimeout();
        log.info("thread wait, threadInfo: {}, timeout: {}", key, timeout);
        THREAD_CACHE.put(threadInfo.getKey(), threadInfo);
        threadWait(key, threadInfo.getTimeout(), isWait);
        if (!threadInfo.getIsNotify()) {
            log.info("thread wait timeout failed, key:{} timeout:{}", key, timeout);
            return Result.failed("thread wait timeout");
        }
        return Result.success(threadInfo.getResult());
    }

    private static void threadWait(String key, Long timeout, AtomicBoolean isWait) {
        synchronized (Thread.currentThread()) {
            try {
                isWait.set(true);
                Thread.currentThread().wait(timeout);
            } catch (InterruptedException e) {
                log.info("thread wait failed, key: {}", key, e);
            }
        }
    }

    /**
     * 唤醒线程
     */
    public static Result<Void> threadNotify(String threadKey, Object result) {
        if (THREAD_CACHE.containsKey(threadKey)) {
            log.info("thread notify key: {}, result: {}", threadKey, result);
            ThreadInfo threadInfo = THREAD_CACHE.get(threadKey);
            threadInfo.setResult(result);
            threadInfo.setIsNotify(true);
            synchronized (threadInfo.getThread()) {
                threadInfo.getThread().notify();
            }
            THREAD_CACHE.remove(threadKey);
        }
        return Result.success();
    }

    /**
     * 异步执行操作，将callable函数的操作结果做为func函数的参数
     *
     * @param <P> 入参类型
     * @param <R> 响应类型
     */
    public static <P, R> Result<R> asyncExec(Callable<Result<P>> callable, Function<P, Result<R>> func) {
        try {
            Result<P> call = callable.call();
            return call.successful() ? func.apply(call.getData()) : Result.failed();
        } catch (Exception e) {
            log.error("function async exec failed.", e);
            return Result.failed();
        }
    }

    /**
     * 线程睡眠
     *
     * @param sleep 睡眠时长: 毫秒
     */
    public static void sleep(long sleep) {
        if (sleep <= 1) {
            return;
        }
        try {
            TimeUnit.MILLISECONDS.sleep(sleep);
        } catch (Exception e) {
            log.error("thread: {} sleep error.", Thread.currentThread().getName(), e);
        }
    }

}
