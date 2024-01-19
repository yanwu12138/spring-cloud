package com.yanwu.spring.cloud.file.controller.file;

import com.yanwu.spring.cloud.common.core.annotation.AccessLimit;
import com.yanwu.spring.cloud.common.core.annotation.RequestHandler;
import com.yanwu.spring.cloud.common.pojo.Result;
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


    @RequestHandler
    @PutMapping("index")
    public Result<Void> indexCreate(@RequestBody EsIndex param) throws Exception {
        if (elasticsearchService.indexExists(param)) {
            return Result.failed("索引已存在");
        }
        elasticsearchService.indexCreate(param);
        return Result.success();
    }

    @RequestHandler
    @AccessLimit(needLogin = false)
    @GetMapping("index")
    public Result<Map<String, MappingMetaData>> indexSelect(@RequestBody EsIndex param) throws Exception {
        if (!elasticsearchService.indexExists(param)) {
            return Result.failed("索引不存在");
        }
        return Result.success(elasticsearchService.indexSelect(param));
    }

    @RequestHandler
    @DeleteMapping("index")
    public Result<Void> indexDelete(@RequestBody EsIndex param) throws Exception {
        if (elasticsearchService.indexExists(param)) {
            elasticsearchService.indexDelete(param);
        }
        return Result.success();
    }

    @RequestHandler
    @GetMapping("index/exists")
    public Result<Boolean> indexExists(@RequestBody EsIndex param) throws Exception {
        return Result.success(elasticsearchService.indexExists(param));
    }


    // ============================== 类型(type) ============================== //


    @RequestHandler
    @PutMapping("type")
    public Result<Void> typeCreate(@RequestBody EsType param) throws Exception {
        if (!elasticsearchService.indexExists(param.getIndex())) {
            indexCreate(param.getIndex());
        }
        if (elasticsearchService.typeExists(param)) {
            return Result.failed("类型已存在");
        }
        elasticsearchService.typeCreate(param);
        return Result.success();
    }

    @RequestHandler
    @PutMapping("type/bulk/{index}")
    public Result<Void> typeCreate(@PathVariable("index") String index,
                                   @RequestBody List<EsType> param) throws Exception {
        EsIndex instance = EsIndex.getInstance(index);
        if (!elasticsearchService.indexExists(instance)) {
            indexCreate(instance);
        }
        elasticsearchService.typeBulkCreate(param);
        return Result.success();
    }

    @RequestHandler
    @GetMapping("type")
    public Result<GetResponse> typeSelect(@RequestBody EsType param) throws Exception {
        return Result.success(elasticsearchService.typeSelect(param));
    }

    @RequestHandler
    @PostMapping("type")
    public Result<Void> typeUpdate(@RequestBody EsType param) throws Exception {
        if (elasticsearchService.typeExists(param)) {
            elasticsearchService.typeUpdate(param);
        } else {
            elasticsearchService.typeCreate(param);
        }
        return Result.success();
    }

    @RequestHandler
    @PostMapping("type/bulk/{index}")
    public Result<Void> typeUpdate(@PathVariable("index") String index,
                                   @RequestBody List<EsType> param) throws Exception {
        EsIndex instance = EsIndex.getInstance(index);
        if (!elasticsearchService.indexExists(instance)) {
            indexCreate(instance);
        }
        elasticsearchService.typeBulkUpdate(param);
        return Result.success();
    }

    @RequestHandler
    @DeleteMapping("type")
    public Result<Void> typeDelete(@RequestBody EsType param) throws Exception {
        if (elasticsearchService.typeExists(param)) {
            elasticsearchService.typeDelete(param);
        }
        return Result.success();
    }

    @RequestHandler
    @DeleteMapping("type/bulk/{index}")
    public Result<Void> typeDelete(@PathVariable("index") String index,
                                   @RequestBody List<EsType> param) throws Exception {
        EsIndex instance = EsIndex.getInstance(index);
        if (elasticsearchService.indexExists(instance)) {
            elasticsearchService.typeBulkDelete(param);
        }
        return Result.success();
    }

    @RequestHandler
    @GetMapping("type/exists")
    public Result<Boolean> typeExists(@RequestBody EsType param) throws Exception {
        return Result.success(elasticsearchService.typeExists(param));
    }

    @RequestHandler
    @GetMapping("type/search")
    public Result<List<EsTypeData>> typeSearch(@RequestBody EsSearch param) throws Exception {
        if (!elasticsearchService.indexExists(param.getType().getIndex())) {
            return Result.failed("索引不存在");
        }
        return Result.success(elasticsearchService.typeSearch(param));
    }


}
