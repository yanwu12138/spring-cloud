package com.yanwu.spring.cloud.file.controller.file;

import com.yanwu.spring.cloud.common.core.annotation.LogParam;
import com.yanwu.spring.cloud.common.pojo.ResponseEnvelope;
import com.yanwu.spring.cloud.common.pojo.Result;
import com.yanwu.spring.cloud.common.utils.ZookeeperLock;
import com.yanwu.spring.cloud.file.cache.ZookeeperClient;
import com.yanwu.spring.cloud.file.pojo.ZookeeperNode;
import com.yanwu.spring.cloud.file.service.ZookeeperService;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * @author Baofeng Xu
 * @date 2021/1/22 17:57.
 * <p>
 * description:
 */
@Slf4j
@RestController
@RequestMapping("file/zookeeper/")
public class ZookeeperController {

    @Resource
    private Executor commonsExecutors;
    @Resource
    private ZookeeperClient zookeeperClient;
    @Resource
    private ZookeeperService zookeeperService;

    @LogParam
    @PostMapping("create")
    public ResponseEnvelope<Long> create(@RequestBody ZookeeperNode param) throws Exception {
        zookeeperService.create(param);
        return ResponseEnvelope.success();
    }

    @LogParam
    @DeleteMapping("delete")
    public ResponseEnvelope<Long> delete(@RequestBody ZookeeperNode param) throws Exception {
        zookeeperService.delete(param);
        return ResponseEnvelope.success();
    }

    @LogParam
    @PostMapping("update")
    public ResponseEnvelope<Long> update(@RequestBody ZookeeperNode param) throws Exception {
        zookeeperService.update(param);
        return ResponseEnvelope.success();
    }

    @LogParam
    @GetMapping("search")
    public ResponseEnvelope<ZookeeperNode> search(@RequestBody ZookeeperNode param) throws Exception {
        return ResponseEnvelope.success(zookeeperService.search(param));
    }

    @LogParam
    @GetMapping("children")
    public ResponseEnvelope<List<String>> children(@RequestBody ZookeeperNode param) throws Exception {
        return ResponseEnvelope.success(zookeeperService.children(param));
    }

    @LogParam
    @GetMapping("lock")
    public ResponseEnvelope<Void> lock(@RequestBody ZookeeperNode param) throws Exception {
        zookeeperService.lock(param.getPath());
        return ResponseEnvelope.success();
    }

    @LogParam
    @GetMapping("test")
    public ResponseEnvelope<Void> test(@RequestBody ZookeeperNode param) {
        CuratorFramework client = zookeeperClient.getClient();
        for (int i = 0; i < 2; i++) {
            commonsExecutors.execute(() -> {
                Result<String> writeResult = ZookeeperLock.writeExecutor(client, param.getPath(), () -> {
                    log.info("write 11111111 {}", Thread.currentThread().getName());
                    TimeUnit.SECONDS.sleep(2);
                    return Result.success("success");
                });
                log.info("write result: {}", writeResult);
            });
        }
        for (int i = 0; i < 8; i++) {
            commonsExecutors.execute(() -> {
                Result<String> readResult = ZookeeperLock.readExecutor(client, param.getPath(), () -> {
                    log.info("read 222222222 {}", Thread.currentThread().getName());
                    TimeUnit.SECONDS.sleep(1);
                    return Result.success("success");
                });
                log.info("read result: {}", readResult);
            });
        }
        return ResponseEnvelope.success();
    }

}
