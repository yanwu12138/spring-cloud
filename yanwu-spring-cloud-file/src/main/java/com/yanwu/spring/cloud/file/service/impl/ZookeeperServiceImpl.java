package com.yanwu.spring.cloud.file.service.impl;

import com.yanwu.spring.cloud.common.utils.ZookeeperLock;
import com.yanwu.spring.cloud.file.cache.ZookeeperClient;
import com.yanwu.spring.cloud.file.pojo.ZookeeperNode;
import com.yanwu.spring.cloud.file.service.ZookeeperService;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * @author Baofeng Xu
 * @date 2021/1/22 17:58.
 * <p>
 * description:
 */
@Slf4j
@Service
public class ZookeeperServiceImpl implements ZookeeperService {

    @Resource
    private Executor commonsExecutors;
    @Resource
    private ZookeeperClient zookeeperClient;

    @Override
    public void create(ZookeeperNode param) throws Exception {
        CuratorFramework client = zookeeperClient.getClient();
        client.create().orSetData().forPath(param.getPath(), param.getValue().getBytes());
    }

    @Override
    public void delete(ZookeeperNode param) throws Exception {
        CuratorFramework client = zookeeperClient.getClient();
        client.delete().forPath(param.getPath());
    }

    @Override
    public void update(ZookeeperNode param) throws Exception {
        CuratorFramework client = zookeeperClient.getClient();
        client.setData().forPath(param.getPath(), param.getValue().getBytes());
    }

    @Override
    public ZookeeperNode search(ZookeeperNode param) throws Exception {
        CuratorFramework client = zookeeperClient.getClient();
        try {
            byte[] bytes = client.getData().storingStatIn(new Stat()).forPath(param.getPath());
            return ZookeeperNode.newInstance(param.getPath(), new String(bytes));
        } catch (KeeperException e) {
            log.error("search zookeeper path failed. param: {}", param, e);
            return null;
        }
    }

    @Override
    public List<String> children(ZookeeperNode param) throws Exception {
        CuratorFramework client = zookeeperClient.getClient();
        return client.getChildren().storingStatIn(new Stat()).forPath(param.getPath());
    }

    @Override
    public void lock(String path) {
        CuratorFramework client = zookeeperClient.getClient();
        int size = 50;
        while (size > 0) {
            commonsExecutors.execute(() -> {
                InterProcessLock lock = ZookeeperLock.getInterProcessSemaphoreMutex(client, path);
                try {
                    lock.acquire();
                    log.info("thread: {} lock", Thread.currentThread().getName());
                    TimeUnit.SECONDS.sleep(2);
                    log.info("thread: {} unlock", Thread.currentThread().getName());
                } catch (Exception e) {
                    log.error("zookeeper lock run error.", e);
                } finally {
                    try {
                        lock.release();
                    } catch (Exception e) {
                        log.error("zookeeper unlock error.", e);
                    }
                }
            });
            size--;
        }
    }

}
