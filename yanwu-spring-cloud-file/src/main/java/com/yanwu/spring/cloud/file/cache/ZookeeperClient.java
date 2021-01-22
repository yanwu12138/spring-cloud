package com.yanwu.spring.cloud.file.cache;

import com.yanwu.spring.cloud.file.config.FileConfig;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author Baofeng Xu
 * @date 2021/1/22 18:23.
 * <p>
 * description:
 */
@Component
public class ZookeeperClient {

    @Resource
    private FileConfig fileConfig;

    private static CuratorFramework curatorFramework;

    @PostConstruct
    public void init() {
        curatorFramework = CuratorFrameworkFactory
                .builder()
                .connectString(fileConfig.getZookeeperPath())
                .sessionTimeoutMs(fileConfig.getZookeeperTimeout())
                .retryPolicy(new ExponentialBackoffRetry(fileConfig.getZookeeperBaseSleepTime(), fileConfig.getZookeeperMaxRetries()))
                .namespace("").build();
        curatorFramework.start();
    }

    public CuratorFramework getClient() {
        return curatorFramework;
    }

}
