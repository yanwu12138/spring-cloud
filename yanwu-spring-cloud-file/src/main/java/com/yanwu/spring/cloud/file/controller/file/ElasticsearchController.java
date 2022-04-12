package com.yanwu.spring.cloud.file.controller.file;

import com.yanwu.spring.cloud.common.core.annotation.AccessLimit;
import com.yanwu.spring.cloud.common.core.annotation.LogParam;
import com.yanwu.spring.cloud.common.pojo.ResponseEnvelope;
import com.yanwu.spring.cloud.file.pojo.elasticsearch.EsIndex;
import com.yanwu.spring.cloud.file.pojo.elasticsearch.EsSearch;
import com.yanwu.spring.cloud.file.pojo.elasticsearch.EsType;
import com.yanwu.spring.cloud.file.pojo.elasticsearch.EsTypeData;
import com.yanwu.spring.cloud.file.service.ElasticsearchService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

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
    @PutMapping("index")
    public ResponseEnvelope<Void> indexCreate(@RequestBody EsIndex param) throws Exception {
        if (elasticsearchService.indexExists(param)) {
            return ResponseEnvelope.failed("索引已存在");
        }
        elasticsearchService.indexCreate(param);
        return ResponseEnvelope.success();
    }

    @LogParam
    @AccessLimit
    @GetMapping("index")
    public ResponseEnvelope<Map<String, MappingMetaData>> indexSelect(@RequestBody EsIndex param) throws Exception {
        if (!elasticsearchService.indexExists(param)) {
            return ResponseEnvelope.failed("索引不存在");
        }
        return ResponseEnvelope.success(elasticsearchService.indexSelect(param));
    }

    @LogParam
    @DeleteMapping("index")
    public ResponseEnvelope<Void> indexDelete(@RequestBody EsIndex param) throws Exception {
        if (elasticsearchService.indexExists(param)) {
            elasticsearchService.indexDelete(param);
        }
        return ResponseEnvelope.success();
    }

    @LogParam
    @GetMapping("index/exists")
    public ResponseEnvelope<Boolean> indexExists(@RequestBody EsIndex param) throws Exception {
        return ResponseEnvelope.success(elasticsearchService.indexExists(param));
    }


    // ============================== 类型(type) ============================== //


    @LogParam
    @PutMapping("type")
    public ResponseEnvelope<Void> typeCreate(@RequestBody EsType param) throws Exception {
        if (!elasticsearchService.indexExists(param.getIndex())) {
            indexCreate(param.getIndex());
        }
        if (elasticsearchService.typeExists(param)) {
            return ResponseEnvelope.failed("类型已存在");
        }
        elasticsearchService.typeCreate(param);
        return ResponseEnvelope.success();
    }

    @LogParam
    @PutMapping("type/bulk/{index}")
    public ResponseEnvelope<Void> typeCreate(@PathVariable("index") String index,
                                             @RequestBody List<EsType> param) throws Exception {
        EsIndex instance = EsIndex.getInstance(index);
        if (!elasticsearchService.indexExists(instance)) {
            indexCreate(instance);
        }
        elasticsearchService.typeBulkCreate(param);
        return ResponseEnvelope.success();
    }

    @LogParam
    @GetMapping("type")
    public ResponseEnvelope<GetResponse> typeSelect(@RequestBody EsType param) throws Exception {
        return ResponseEnvelope.success(elasticsearchService.typeSelect(param));
    }

    @LogParam
    @PostMapping("type")
    public ResponseEnvelope<Void> typeUpdate(@RequestBody EsType param) throws Exception {
        if (elasticsearchService.typeExists(param)) {
            elasticsearchService.typeUpdate(param);
        } else {
            elasticsearchService.typeCreate(param);
        }
        return ResponseEnvelope.success();
    }

    @LogParam
    @PostMapping("type/bulk/{index}")
    public ResponseEnvelope<Void> typeUpdate(@PathVariable("index") String index,
                                             @RequestBody List<EsType> param) throws Exception {
        EsIndex instance = EsIndex.getInstance(index);
        if (!elasticsearchService.indexExists(instance)) {
            indexCreate(instance);
        }
        elasticsearchService.typeBulkUpdate(param);
        return ResponseEnvelope.success();
    }

    @LogParam
    @DeleteMapping("type")
    public ResponseEnvelope<Void> typeDelete(@RequestBody EsType param) throws Exception {
        if (elasticsearchService.typeExists(param)) {
            elasticsearchService.typeDelete(param);
        }
        return ResponseEnvelope.success();
    }

    @LogParam
    @DeleteMapping("type/bulk/{index}")
    public ResponseEnvelope<Void> typeDelete(@PathVariable("index") String index,
                                             @RequestBody List<EsType> param) throws Exception {
        EsIndex instance = EsIndex.getInstance(index);
        if (elasticsearchService.indexExists(instance)) {
            elasticsearchService.typeBulkDelete(param);
        }
        return ResponseEnvelope.success();
    }

    @LogParam
    @GetMapping("type/exists")
    public ResponseEnvelope<Boolean> typeExists(@RequestBody EsType param) throws Exception {
        return ResponseEnvelope.success(elasticsearchService.typeExists(param));
    }

    @LogParam
    @GetMapping("type/search")
    public ResponseEnvelope<List<EsTypeData>> typeSearch(@RequestBody EsSearch param) throws Exception {
        if (!elasticsearchService.indexExists(param.getType().getIndex())) {
            return ResponseEnvelope.failed("索引不存在");
        }
        return ResponseEnvelope.success(elasticsearchService.typeSearch(param));
    }


}
