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
        ThreadInfo instance = ThreadInfo.getInstance(ThreadUtil.getUniOnlyId(), 5000L);
        AtomicBoolean isWait = new AtomicBoolean(false);

        new Thread(() -> {
            ThreadUtil.sleep(2000L);
            for (int i = 0; i < 200; i++) {
                if (isWait.get()) {
                    Result<Void> notify = ThreadUtil.threadNotify(instance.getKey(), "线程唤醒1111111111");
                    log.info("thread notify result: {}", notify);
                    return;
                }
                ThreadUtil.sleep(100L);
            }
        }).start();

        Result<?> wait = ThreadUtil.threadWait(instance, isWait);
        log.info("thread wait result: {}", wait);
    }

    /**
     * 获取一个唯一的自增ID
     */
    public static String getUniOnlyId() {
        return String.valueOf(THREAD_TASK_ID.getAndIncrement());
    }

    /**
     * 将当前线程阻塞，等待其它线程唤醒当前线程
     *
     * @param threadInfo 当前线程，需要被阻塞
     * @param isWait     是否已经成功阻塞当前线程
     */
    public static Result<?> threadWait(ThreadInfo threadInfo, AtomicBoolean isWait) {
        log.info("thread wait, threadInfo: {}", threadInfo);
        threadInfoWait(threadInfo, isWait);
        if (!threadInfo.getIsNotify()) {
            THREAD_CACHE.remove(threadInfo.getKey());
            log.info("thread wait timeout failed, threadInfo: {}", threadInfo);
            return Result.failed("thread wait timeout.");
        }
        return Result.success(threadInfo.getResult());
    }

    /**
     * 将当前线程阻塞
     *
     * @param threadInfo 当前线程，需要被阻塞
     * @param isWait     是否已经成功阻塞当前线程的标识
     */
    private static void threadInfoWait(ThreadInfo threadInfo, AtomicBoolean isWait) {
        synchronized (threadInfo.getThread()) {
            try {
                isWait.compareAndSet(false, true);
                THREAD_CACHE.put(threadInfo.getKey(), threadInfo);
                threadInfo.getThread().wait(threadInfo.getTimeout());
            } catch (InterruptedException e) {
                log.info("thread wait failed, threadInfo: {}", threadInfo, e);
            }
        }
    }

    /**
     * 根据线程的唯一标识唤醒执行线程
     *
     * @param threadKey 阻塞线程的唯一标识
     * @param result    唤醒阻塞线程时传递给阻速线程的返回值
     */
    public static Result<Void> threadNotify(String threadKey, Object result) {
        if (!THREAD_CACHE.containsKey(threadKey)) {
            log.error("thread notify failed, because no threadKey in cache. threadKey: {}, result: {}", threadKey, result);
            return Result.failed("no threadKey in cache");
        }
        ThreadInfo threadInfo = THREAD_CACHE.get(threadKey);
        log.info("thread notify threadInfo: {}, result: {}", threadInfo, result);
        synchronized (threadInfo.getThread()) {
            threadInfo.setResult(result);
            threadInfo.setIsNotify(true);
            THREAD_CACHE.remove(threadKey);
            threadInfo.getThread().notify();
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
