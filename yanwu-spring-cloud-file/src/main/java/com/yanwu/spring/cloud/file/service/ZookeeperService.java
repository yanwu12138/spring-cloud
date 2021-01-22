package com.yanwu.spring.cloud.file.service;

import com.yanwu.spring.cloud.file.pojo.ZookeeperNode;

import java.util.List;

/**
 * @author Baofeng Xu
 * @date 2021/1/22 17:58.
 * <p>
 * description:
 */
public interface ZookeeperService {

    /**
     * 创建节点
     *
     * @param param 节点信息
     * @throws Exception e
     */
    void create(ZookeeperNode param) throws Exception;

    /**
     * 删除节点
     *
     * @param param 节点信息
     * @throws Exception e
     */
    void delete(ZookeeperNode param) throws Exception;

    /**
     * 更改节点
     *
     * @param param 节点信息
     * @throws Exception e
     */
    void update(ZookeeperNode param) throws Exception;

    /**
     * 查找结点数据
     *
     * @param param 节点信息
     * @return 结点数据
     * @throws Exception e
     */
    ZookeeperNode search(ZookeeperNode param) throws Exception;

    /**
     * 查找目录下所有的节点
     *
     * @param param 节点信息
     * @return 节点下的子节点
     * @throws Exception e
     */
    List<String> children(ZookeeperNode param) throws Exception;

    /**
     * 测试zookeeper分布式锁
     *
     * @param path 加锁路径
     */
    void lock(String path);
}
