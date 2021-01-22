package com.yanwu.spring.cloud.common.utils;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.*;

import java.util.List;

/**
 * @author Baofeng Xu
 * @date 2021/1/22 18:48.
 * <p>
 * description: zookeeper实现分布式锁
 */
@SuppressWarnings("unused")
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

}
