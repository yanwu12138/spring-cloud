package com.yanwu.spring.cloud.common.utils;

import com.yanwu.spring.cloud.common.pojo.Result;
import com.yanwu.spring.cloud.common.pojo.ThreadInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
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

    public static void main(String[] args) {
        Result<String> result = asyncExec(() -> {
            log.info("function exec 1111");
            return Result.success("function callable result. " + SystemUtil.getSystemType());
        }, param -> {
            log.info("function exec 2222, param: {}", param);
            return Result.success("function func result.");
        });
        log.info("function exec 3333, result: {}", result);

    }

    /**
     * 获取一个唯一的自增ID
     */
    public static long getUniId() {
        return THREAD_TASK_ID.getAndIncrement();
    }

    /**
     * 线程等待
     */
    public static Result<Object> threadWait(ThreadInfo threadInfo, AtomicBoolean isWait) throws Exception {
        String key = threadInfo.getKey();
        Long timeout = threadInfo.getTimeout();
        log.info("thread wait, threadInfo: {}, timeout: {}", key, timeout);
        THREAD_CACHE.put(threadInfo.getKey(), threadInfo);
        threadWait(threadInfo.getTimeout(), isWait);
        if (!threadInfo.getIsNotify()) {
            log.info("thread wait timeout failed, key:{} timeout:{}", key, timeout);
            throw new TimeoutException("thread wait timeout, key: " + key + ", timeout: " + timeout);
        }
        return threadInfo.getResult();
    }

    private static void threadWait(Long timeout, AtomicBoolean isWait) {
        synchronized (Thread.currentThread()) {
            try {
                isWait.set(true);
                Thread.currentThread().wait(timeout);
            } catch (InterruptedException e) {
                log.info("thread wait timeout failed, timeout:{}", timeout, e);
            }
        }
    }

    /**
     * 唤醒线程
     */
    public static void threadNotify(String threadKey, Object result) {
        log.info("thread notify key: {}, result: {}", threadKey, result);
        if (THREAD_CACHE.containsKey(threadKey)) {
            ThreadInfo threadInfo = THREAD_CACHE.get(threadKey);
            threadInfo.setResult(result);
            threadInfo.setIsNotify(true);
            synchronized (threadInfo.getThread()) {
                threadInfo.getThread().notify();
            }
            THREAD_CACHE.remove(threadKey);
        }
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
            return call.isSuccess() ? func.apply(call.getData()) : Result.failed();
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
