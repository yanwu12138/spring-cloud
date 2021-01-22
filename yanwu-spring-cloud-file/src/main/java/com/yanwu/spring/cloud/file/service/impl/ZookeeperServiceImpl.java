package com.yanwu.spring.cloud.file.service.impl;

import com.yanwu.spring.cloud.file.cache.ZookeeperClient;
import com.yanwu.spring.cloud.file.pojo.ZookeeperNode;
import com.yanwu.spring.cloud.file.service.ZookeeperService;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
        byte[] bytes = client.getData().storingStatIn(new Stat()).forPath(param.getPath());
        return ZookeeperNode.getInstance(param.getPath(), new String(bytes));
    }

}
