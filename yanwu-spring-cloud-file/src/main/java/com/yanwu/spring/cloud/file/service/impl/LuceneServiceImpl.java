package com.yanwu.spring.cloud.file.service.impl;

import com.yanwu.spring.cloud.common.pojo.PageParam;
import com.yanwu.spring.cloud.file.cache.LuceneCacheManager;
import com.yanwu.spring.cloud.file.config.FileConfig;
import com.yanwu.spring.cloud.file.pojo.LuceneDocument;
import com.yanwu.spring.cloud.file.pojo.LuceneSearch;
import com.yanwu.spring.cloud.file.service.LuceneService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Baofeng Xu
 * @date 2021/1/22 10:04.
 * <p>
 * description:
 */
@Slf4j
@Service
public class LuceneServiceImpl implements LuceneService {
    @Resource
    private FileConfig fileConfig;
    @Resource
    private LuceneCacheManager cacheManager;

    /**
     * 创建索引
     */
    @Override
    public Long create(LuceneDocument param) throws Exception {
        Long index = cacheManager.getIdIndex();
        Document document = createDocument(index, param);
        Directory directory = cacheManager.getDirectory(fileConfig.getLuceneIndex());
        try (IndexWriter writer = cacheManager.getWriter(directory)) {
            writer.addDocument(document);
            writer.commit();
        }
        return index;
    }

    /**
     * 删除索引
     */
    @Override
    public Long delete(String field, String value) throws Exception {
        long result;
        Directory directory = cacheManager.getDirectory(fileConfig.getLuceneIndex());
        try (IndexWriter writer = cacheManager.getWriter(directory)) {
            writer.deleteDocuments(new QueryParser(field, new SmartChineseAnalyzer()).parse(value));
            result = writer.deleteAll();
            writer.commit();
        }
        return result;
    }

    /**
     * 更新索引
     */
    @Override
    public void update(Long id, String field, String value) throws Exception {
        Directory directory = cacheManager.getDirectory(fileConfig.getLuceneIndex());
        try (IndexWriter writer = cacheManager.getWriter(directory)) {
            Document doc = new Document();
            doc.add(new TextField(field, value, Field.Store.YES));
            writer.updateDocument(new Term("id", String.valueOf(id)), doc);
            writer.commit();
        }
    }

    /**
     * 查询所有
     */
    @Override
    public List<LuceneDocument> searchAll(LuceneSearch param) throws Exception {
        Query query = new MultiFieldQueryParser(param.getFields(), new SmartChineseAnalyzer()).parse(param.getValue());
        Directory directory = cacheManager.getDirectory(fileConfig.getLuceneIndex());
        try (IndexReader reader = cacheManager.getReader(directory)) {
            // ----- TopDocs：搜索结果的信息集合
            TopDocs topDocs = getTopDocs(new IndexSearcher(reader), query, param, Integer.MAX_VALUE);
            return assemblyResult(reader, topDocs.scoreDocs, null, null);
        }
    }

    /**
     * 分页查询，
     */
    @Override
    public List<LuceneDocument> pageSearch(PageParam<LuceneSearch> param) throws Exception {
        param.setPage(param.getPage() > 0 ? param.getPage() : 1);
        Query query = new MultiFieldQueryParser(param.getData().getFields(), new SmartChineseAnalyzer()).parse(param.getData().getValue());
        // ----- 计算起始角标
        int start = (param.getPage() - 1) * param.getSize();
        // ----- 计算结束角标
        int end = start + param.getSize();
        Directory directory = cacheManager.getDirectory(fileConfig.getLuceneIndex());
        try (IndexReader reader = cacheManager.getReader(directory)) {
            TopDocs topDocs = getTopDocs(new IndexSearcher(reader), query, param.getData(), end);
            return assemblyResult(reader, topDocs.scoreDocs, start, end);
        }
    }

    /**
     * 创建文档
     */
    private Document createDocument(Long id, LuceneDocument param) {
        Document document = new Document();
        // ----- 添加字段,这里字段的参数：字段的名称、字段的值、是否存储。Store.YES存储，Store.NO是不存储
        document.add(new StringField("id", String.valueOf(id), Field.Store.YES));
        document.add(new TextField("title", param.getTitle(), Field.Store.YES));
        document.add(new TextField("content", param.getContent(), Field.Store.YES));
        return document;
    }

    /**
     * 组装查询条件
     */
    private TopDocs getTopDocs(IndexSearcher searcher, Query query, LuceneSearch param, Integer end) throws Exception {
        TopDocs topDocs;
        if (StringUtils.isBlank(param.getSortField())) {
            topDocs = searcher.search(query, end);
        } else {
            Sort sort = param.getSort() == null ?
                    new Sort(new SortField(param.getSortField(), SortField.Type.DOC)) :
                    new Sort(new SortField(param.getSortField(), SortField.Type.DOC, param.getSort()));
            topDocs = searcher.search(query, end, sort);
        }
        return topDocs;
    }

    /**
     * 组装查询结果返回
     */
    private List<LuceneDocument> assemblyResult(IndexReader reader, ScoreDoc[] scoreDocs, Integer start, Integer end) throws Exception {
        List<LuceneDocument> result = new ArrayList<>();
        start = start == null ? 0 : start;
        end = end == null ? scoreDocs.length : end;
        for (int i = start; i < end; i++) {
            if (i >= scoreDocs.length) {
                break;
            }
            // ----- ScoreDoc：搜索到的某个文档信息
            ScoreDoc scoreDoc = scoreDocs[i];
            Document document = reader.document(scoreDoc.doc);
            result.add(LuceneDocument.getInstance(document.get("id"), document.get("title"), document.get("content")));
        }
        return result;
    }

}
