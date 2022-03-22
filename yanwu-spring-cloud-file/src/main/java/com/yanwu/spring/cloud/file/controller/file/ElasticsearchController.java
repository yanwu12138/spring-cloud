package com.yanwu.spring.cloud.file.controller.file;

import com.yanwu.spring.cloud.common.core.annotation.LogParam;
import com.yanwu.spring.cloud.common.pojo.ResponseEnvelope;
import com.yanwu.spring.cloud.file.pojo.elasticsearch.BaseIndex;
import com.yanwu.spring.cloud.file.pojo.elasticsearch.BaseType;
import com.yanwu.spring.cloud.file.service.ElasticsearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author Baofeng Xu
 * @date 2022/3/21 17:22.
 * <p>
 * description:
 */
@Slf4j
@RestController
@RequestMapping("file/elasticsearch/")
public class ElasticsearchController {

    @Resource
    private ElasticsearchService elasticsearchService;

    // ============================== 索引(index) ============================== //


    @LogParam
    @PostMapping("index/create")
    public ResponseEnvelope<Void> indexCreate(@RequestBody BaseIndex<?> param) throws Exception {
        if (elasticsearchService.indexExists(param)) {
            return ResponseEnvelope.failed("索引已存在");
        }
        elasticsearchService.indexCreate(param);
        return ResponseEnvelope.success();
    }

    @LogParam
    @GetMapping("index/exists")
    public ResponseEnvelope<Boolean> indexExists(@RequestBody BaseIndex<?> param) throws Exception {
        return ResponseEnvelope.success(elasticsearchService.indexExists(param));
    }

    @LogParam
    @DeleteMapping("index/delete")
    public ResponseEnvelope<Void> indexDelete(@RequestBody BaseIndex<?> param) throws Exception {
        if (elasticsearchService.indexExists(param)) {
            elasticsearchService.indexDelete(param);
        }
        return ResponseEnvelope.success();
    }


    // ============================== 类型(type) ============================== //


    @LogParam
    @PostMapping("type/create")
    public ResponseEnvelope<Void> typeCreate(@RequestBody BaseIndex<BaseType<?>> param) throws Exception {
        if (!elasticsearchService.indexExists(param)) {
            indexCreate(param);
        }
        if (elasticsearchService.typeExists(param)) {
            return ResponseEnvelope.failed("类型已存在");
        }
        elasticsearchService.typeAdd(param);
        return ResponseEnvelope.success();
    }

    @LogParam
    @GetMapping("type/exists")
    public ResponseEnvelope<Boolean> typeExists(@RequestBody BaseIndex<BaseType<?>> param) throws Exception {
        return ResponseEnvelope.success(elasticsearchService.typeExists(param));
    }


}
