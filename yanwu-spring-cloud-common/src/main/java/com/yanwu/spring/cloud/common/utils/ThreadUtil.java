package com.yanwu.spring.cloud.common.utils;

import com.yanwu.spring.cloud.common.pojo.Result;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author Baofeng Xu
 * @date 2021/4/26 14:39.
 * <p>
 * description:
 */
@Slf4j
public class ThreadUtil {

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
