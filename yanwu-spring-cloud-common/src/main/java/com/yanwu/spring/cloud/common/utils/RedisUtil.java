package com.yanwu.spring.cloud.common.utils;

import com.yanwu.spring.cloud.common.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/6/9 16:10.
 * <p>
 * description:
 */
@Slf4j
@Component
@SuppressWarnings("unused")
public class RedisUtil {
    /*** 分布式锁固定前缀 ***/
    private static final String REDIS_LOCK = "redis_lock_";
    private static final String REDIS_SEQ = "redis_seq_";
    /*** 分布式锁过期时间 ***/
    private static final Integer EXPIRE_TIME = 30;
    /*** 每次自旋睡眠时间 ***/
    private static final Integer SLEEP_TIME = 100;
    /*** 分布式锁自旋次数 ***/
    private static final Integer CYCLES = 100;
    /*** 分布式锁处理器 ***/
    @SuppressWarnings("all")
    @Resource(name = "redisTemplate")
    private ValueOperations<String, String> operations;
    /*** 事务处理器 ***/
    @SuppressWarnings("all")
    @Resource(name = "redisTemplate")
    private RedisOperations<?, ?> executeOperations;

    private RedisUtil() {
    }

    /**
     * 获取一个sequence序列号
     *
     * @param key KEY
     * @return 自增系列号
     */
    public Long increment(String key) {
        Result<Long> result = executor(key, () -> {
            Long increment = operations.increment(key(REDIS_SEQ, key));
            return Result.success(increment);
        });
        return result.getData();
    }

    /**
     * sequence序列号减一
     *
     * @param key KEY
     * @return 自增系列号
     */
    public Long decrement(String key) {
        Result<Long> result = executor(key, () -> {
            Long decrement = operations.decrement(key(REDIS_SEQ, key));
            return Result.success(decrement);
        });
        return result.getData();
    }

    /**
     * 加锁，超时时间为默认的30S
     *
     * @param key   加锁唯一标识
     * @param value 释放锁唯一标识（建议使用线程ID作为value）
     * @return [true: 上锁成功; false: 上锁失败]
     */
    public boolean lock(String key, Long value) {
        return lock(key, value, EXPIRE_TIME);
    }

    /**
     * 加锁
     *
     * @param key     加锁唯一标识
     * @param value   释放锁唯一标识（建议使用线程ID作为value）
     * @param timeout 超时时间（单位：S）
     * @return [true: 上锁成功; false: 上锁失败]
     */
    public boolean lock(String key, Long value, Integer timeout) {
        Assert.isTrue(StringUtils.isNotBlank(key), "redis locks are identified as null.");
        Assert.isTrue((value != null), "the redis release lock is identified as null.");
        int cycles = CYCLES;
        // ----- 尝试获取锁，加锁成功直接返回，否则，循环尝试获取
        while (!tryLock(key, value, timeout)) {
            if (0 == (cycles--)) {
                // ----- 最多循环100次（10S），当尝试了10S都没有获取到锁，则认为获取锁失败
                log.error("redis lock failed. key: {}, value: {}", key, value);
                return false;
            }
            ThreadUtil.sleep(SLEEP_TIME);
        }
        return true;
    }

    /**
     * 尝试获取锁
     *
     * @param key     加锁唯一标识
     * @param value   释放锁唯一标识（建议使用线程ID作为value）
     * @param timeout 超时时间（单位：S）
     * @return [true: 加锁成功; false: 加锁失败]
     */
    private boolean tryLock(String key, Long value, Integer timeout) {
        Boolean result = operations.setIfAbsent(key(REDIS_LOCK, key), String.valueOf(value), timeout, TimeUnit.SECONDS);
        return result != null && result;
    }

    /**
     * 释放锁
     *
     * @param key   加锁唯一标识
     * @param value 释放锁唯一标识（建议使用线程ID作为value）
     */
    public void unLock(String key, Long value) {
        Assert.isTrue(StringUtils.isNotBlank(key), "redis locks are identified as null.");
        Assert.isTrue((value != null), "the redis release lock is identified as null.");
        // ----- 通过value判断是否是该锁：是则释放；不是则不释放，避免误删
        if (!String.valueOf(value).equals(operations.get(key(REDIS_LOCK, key)))) {
            log.error("redis unLock failed. key: {}, value: {}", key, value);
            return;
        }
        operations.getOperations().delete(key(REDIS_LOCK, key));
    }

    private String key(String prefix, String key) {
        return prefix + key;
    }


    /**
     * 执行任务，锁的超时时间为默认的30S，默认使用线程ID作为value
     *
     * @param key      加锁唯一标识
     * @param callable 加锁后执行的任务
     * @return 执行结果返回值
     */
    public <T> Result<T> executor(String key, Callable<Result<T>> callable) {
        return executor(key, Thread.currentThread().getId(), callable);
    }


    /**
     * 执行任务，锁的超时时间为默认的30S
     *
     * @param key      加锁唯一标识
     * @param value    释放锁唯一标识（建议使用线程ID作为value）
     * @param callable 加锁后执行的任务
     * @return 执行结果返回值
     */
    public <T> Result<T> executor(String key, Long value, Callable<Result<T>> callable) {
        return executor(key, value, EXPIRE_TIME, callable);
    }

    /**
     * 执行任务
     *
     * @param key      加锁唯一标识
     * @param value    释放锁唯一标识（建议使用线程ID作为value）
     * @param timeout  超时时间（单位：S）
     * @param callable 加锁后执行的任务
     * @return 执行结果返回值
     */
    public <T> Result<T> executor(String key, Long value, Integer timeout, Callable<Result<T>> callable) {
        if (!lock(key, value, timeout)) {
            log.error("redis run failed because lock failed. key: {}, value: {}", key, value);
            return Result.failed();
        }
        try {
            return callable.call();
        } catch (Exception e) {
            log.error("redis execute callable error.", e);
            return Result.failed();
        } finally {
            unLock(key, value);
        }
    }

    /**
     * 在一个事务内运行多个redis操作
     *
     * @param callable 一组redis操作
     * @return 执行结果返回值
     */
    public <T> Result<T> multiExec(Callable<Result<T>> callable) {
        return executeOperations.execute(new SessionCallback<Result<T>>() {
            @Override
            public <K, V> Result<T> execute(@NonNull RedisOperations<K, V> operations) throws DataAccessException {
                try {
                    // ----- 开启事务
                    operations.multi();
                    // ----- 执行
                    Result<T> result = callable.call();
                    // ----- 提交事务
                    operations.exec();
                    return result;
                } catch (Exception e) {
                    log.error("redis multiExec callable error.", e);
                    return Result.failed();
                }
            }
        });
    }

}
