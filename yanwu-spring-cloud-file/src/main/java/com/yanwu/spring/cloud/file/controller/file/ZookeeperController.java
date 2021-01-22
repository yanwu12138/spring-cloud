package com.yanwu.spring.cloud.file.controller.file;

import com.yanwu.spring.cloud.common.core.annotation.LogParam;
import com.yanwu.spring.cloud.common.pojo.ResponseEnvelope;
import com.yanwu.spring.cloud.file.pojo.ZookeeperNode;
import com.yanwu.spring.cloud.file.service.ZookeeperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

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



}
