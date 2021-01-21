package com.yanwu.spring.cloud.file.controller.file;

import com.yanwu.spring.cloud.common.core.annotation.LogParam;
import com.yanwu.spring.cloud.common.pojo.ResponseEnvelope;
import com.yanwu.spring.cloud.file.cache.LuceneCacheManager;
import com.yanwu.spring.cloud.file.config.FileConfig;
import com.yanwu.spring.cloud.file.pojo.LuceneDocument;
import com.yanwu.spring.cloud.file.utils.LuceneUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

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
    private FileConfig fileConfig;
    @Resource
    private LuceneCacheManager cacheManager;

    /**
     * 创建索引
     *
     * @param param 需要创建索引的内容
     */
    @LogParam
    @PostMapping("create")
    public ResponseEnvelope<Long> create(@RequestBody LuceneDocument param) throws Exception {
        // ----- 创建文档对象
        Long index = cacheManager.getIdIndex();
        Document document = LuceneUtil.createDocument(index, param);
        // ----- 创建索引目录对象
        Directory directory = cacheManager.getDirectory(fileConfig.getLuceneIndex());
        // ----- 创建索引写出工具
        try (IndexWriter writer = cacheManager.getWriter(directory)) {
            // ----- 添加文档到索引写出工具
            writer.addDocument(document);
            // ----- 提交
            writer.commit();
        }
        return ResponseEnvelope.success(index);
    }

    @LogParam
    @PostMapping(value = "delete/{key}/{value}")
    public ResponseEnvelope<Void> delete(@PathVariable("key") String key, @PathVariable("value") String value) throws Exception {

        return ResponseEnvelope.success();
    }

    @LogParam
    @PostMapping(value = "update/{key}/{value}")
    public ResponseEnvelope<Void> update(@PathVariable("key") String key, @PathVariable("value") String value) throws Exception {

        return ResponseEnvelope.success();
    }

    @LogParam
    @PostMapping(value = "select/{key}/{value}")
    public ResponseEnvelope<Void> select(@PathVariable("key") String key, @PathVariable("value") String value) throws Exception {
        Query query = new QueryParser(key, new SmartChineseAnalyzer()).parse(value);
        // ----- 创建索引目录对象
        Directory directory = cacheManager.getDirectory(fileConfig.getLuceneIndex());
        try (IndexReader reader = cacheManager.getReader(directory)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs topDocs = searcher.search(query, 10);
            log.info("本次共搜索到 {} 条数据", topDocs.totalHits);
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            for (ScoreDoc doc : scoreDocs) {
                Document document = reader.document(doc.doc);
                log.info("id: {}, title: {}, content: {}, 得分: {}", document.get("id"), document.get("title"), document.get("content"), doc.score);
            }
        }
        return ResponseEnvelope.success();
    }

}
