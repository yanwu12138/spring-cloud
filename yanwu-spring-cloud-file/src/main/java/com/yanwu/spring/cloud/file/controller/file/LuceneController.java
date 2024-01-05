package com.yanwu.spring.cloud.file.controller.file;

import com.yanwu.spring.cloud.common.core.annotation.RequestLog;
import com.yanwu.spring.cloud.common.pojo.PageParam;
import com.yanwu.spring.cloud.common.pojo.ResponseEnvelope;
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
    @RequestLog
    @PostMapping("create")
    public ResponseEnvelope<Long> create(@RequestBody LuceneDocument param) throws Exception {
        return ResponseEnvelope.success(luceneService.create(param));
    }

    @RequestLog
    @DeleteMapping(value = "delete/{field}/{value}")
    public ResponseEnvelope<Long> delete(@PathVariable("field") String field,
                                         @PathVariable("value") String value) throws Exception {
        return ResponseEnvelope.success(luceneService.delete(field, value));
    }

    @RequestLog
    @PostMapping(value = "update/{id}/{field}/{value}")
    public ResponseEnvelope<Void> update(@PathVariable("id") Long id,
                                         @PathVariable("field") String field,
                                         @PathVariable("value") String value) throws Exception {
        luceneService.update(id, field, value);
        return ResponseEnvelope.success();
    }

    @RequestLog
    @GetMapping(value = "searchAll")
    public ResponseEnvelope<List<LuceneDocument>> searchAll(@RequestBody LuceneSearch param) throws Exception {
        return ResponseEnvelope.success(luceneService.searchAll(param));
    }

    @RequestLog
    @GetMapping(value = "searchPage")
    public ResponseEnvelope<List<LuceneDocument>> searchPage(@RequestBody PageParam<LuceneSearch> param) throws Exception {
        return ResponseEnvelope.success(luceneService.pageSearch(param));
    }

}
