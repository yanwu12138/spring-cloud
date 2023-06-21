package com.yanwu.spring.cloud.common.utils;

import com.yanwu.spring.cloud.common.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.*;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author Baofeng Xu
 * @date 2021/1/22 18:48.
 * <p>
 * description: zookeeper实现分布式锁
 */
@SuppressWarnings("unused")
@Slf4j
public class ZookeeperLock {

    /**
     * 获取分布式可重入排他锁
     *
     * @param client zookeeper客户端
     * @param path   锁路径
     * @return 锁
     */
    public static InterProcessLock getInterProcessMutex(CuratorFramework client, String path) {
        return new InterProcessMutex(client, path);
    }

    /**
     * 获取分布式排它锁
     *
     * @param client zookeeper客户端
     * @param path   锁路径
     * @return 锁
     */
    public static InterProcessLock getInterProcessSemaphoreMutex(CuratorFramework client, String path) {
        return new InterProcessSemaphoreMutex(client, path);
    }

    /**
     * 获取分布式读写锁
     *
     * @param client zookeeper客户端
     * @param path   锁路径
     * @return 锁
     */
    public static InterProcessReadWriteLock getInterProcessReadWriteLock(CuratorFramework client, String path) {
        return new InterProcessReadWriteLock(client, path);
    }

    /**
     * 将多个锁作为单个实体管理的容器
     *
     * @param client zookeeper客户端
     * @param paths  锁路径
     * @return 锁
     */
    public static InterProcessLock getInterProcessMultiLock(CuratorFramework client, List<String> paths) {
        return new InterProcessMultiLock(client, paths);
    }

    /**
     * 执行任务
     *
     * @param client   客户端连接
     * @param path     路径
     * @param callable 加锁后执行的任务
     * @return 执行结果返回值
     */
    public static <T> Result<T> mutexExecutor(CuratorFramework client, String path, Callable<Result<T>> callable) {
        return executor(getInterProcessMutex(client, path), callable);
    }

    /**
     * 使用读写锁执行读任务
     *
     * @param client   客户端连接
     * @param path     路径
     * @param callable 加锁后执行的任务
     * @return 执行结果返回值
     */
    public static <T> Result<T> readExecutor(CuratorFramework client, String path, Callable<Result<T>> callable) {
        return executor(getInterProcessReadWriteLock(client, path).readLock(), callable);
    }

    /**
     * 使用读写锁执行写任务
     *
     * @param client   客户端连接
     * @param path     路径
     * @param callable 加锁后执行的任务
     * @return 执行结果返回值
     */
    public static <T> Result<T> writeExecutor(CuratorFramework client, String path, Callable<Result<T>> callable) {
        return executor(getInterProcessReadWriteLock(client, path).writeLock(), callable);
    }

    /**
     * 使用读写锁执行写任务
     *
     * @param client   客户端连接
     * @param paths    路径
     * @param callable 加锁后执行的任务
     * @return 执行结果返回值
     */
    public static <T> Result<T> multiLockExecutor(CuratorFramework client, List<String> paths, Callable<Result<T>> callable) {
        return executor(getInterProcessMultiLock(client, paths), callable);
    }

    /**
     * 执行任务
     *
     * @param lock     锁
     * @param callable 加锁后执行的任务
     * @return 执行结果返回值
     */
    public static <T> Result<T> executor(InterProcessLock lock, Callable<Result<T>> callable) {
        try {
            lock.acquire();
            return callable.call();
        } catch (Exception e) {
            log.error("zookeeper execute callable error.", e);
            return Result.failed();
        } finally {
            unLock(lock);
        }
    }

    private static void unLock(InterProcessLock lock) {
        try {
            lock.release();
            log.info("zookeeper unLock success. lock: {}", lock);
        } catch (Exception e) {
            log.error("zookeeper unLock failed. lock: {}", lock, e);
        }
    }

}
