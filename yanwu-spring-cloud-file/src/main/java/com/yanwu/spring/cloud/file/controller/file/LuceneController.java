package com.yanwu.spring.cloud.file.controller.file;

import com.yanwu.spring.cloud.common.core.annotation.RequestHandler;
import com.yanwu.spring.cloud.common.pojo.PageParam;
import com.yanwu.spring.cloud.common.pojo.Result;
import com.yanwu.spring.cloud.file.pojo.LuceneDocument;
import com.yanwu.spring.cloud.file.pojo.LuceneSearch;
import com.yanwu.spring.cloud.file.service.LuceneService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Baofeng Xu
 * @date 2021/1/21 11:37.
 * <p>
 * description:
 */
@Slf4j
@RestController
@RequestMapping("file/lucene/")
public class LuceneController {

    @Resource
    private LuceneService luceneService;

    /**
     * 创建索引
     *
     * @param param 需要创建索引的内容
     */
    @RequestHandler
    @PostMapping("create")
    public Result<Long> create(@RequestBody LuceneDocument param) throws Exception {
        return Result.success(luceneService.create(param));
    }

    @RequestHandler
    @DeleteMapping(value = "delete/{field}/{value}")
    public Result<Long> delete(@PathVariable("field") String field,
                               @PathVariable("value") String value) throws Exception {
        return Result.success(luceneService.delete(field, value));
    }

    @RequestHandler
    @PostMapping(value = "update/{id}/{field}/{value}")
    public Result<Void> update(@PathVariable("id") Long id,
                               @PathVariable("field") String field,
                               @PathVariable("value") String value) throws Exception {
        luceneService.update(id, field, value);
        return Result.success();
    }

    @RequestHandler
    @GetMapping(value = "searchAll")
    public Result<List<LuceneDocument>> searchAll(@RequestBody LuceneSearch param) throws Exception {
        return Result.success(luceneService.searchAll(param));
    }

    @RequestHandler
    @GetMapping(value = "searchPage")
    public Result<List<LuceneDocument>> searchPage(@RequestBody PageParam<LuceneSearch> param) throws Exception {
        return Result.success(luceneService.pageSearch(param));
    }

}
