package com.yanwu.spring.cloud.file.cache;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Baofeng Xu
 * @date 2021/1/21 15:21.
 * <p>
 * description:
 */
@Component
public class LuceneCacheManager {
    private static final String LUCENE_ID_INDEX_KEY = "lucene_id_index_key";

    /*** 存储Directory的map */
    private static final Map<String, Directory> DIRECTORY_CACHE = new ConcurrentHashMap<>();

    /*** 存储indexWriter的map */
    private static final Map<Directory, IndexWriter> WRITER_CACHE = new ConcurrentHashMap<>();

    /*** 存储Directory的map */
    private static final Map<Directory, IndexReader> READER_CACHE = new ConcurrentHashMap<>();

    @SuppressWarnings("all")
    @Resource(name = "redisTemplate")
    private ValueOperations<String, String> idOperations;
    private static final AtomicLong INDEX_CACHE = new AtomicLong(0L);

    @PostConstruct
    public void init() {
        String index = idOperations.get(LUCENE_ID_INDEX_KEY);
        if (StringUtils.isNotBlank(index)) {
            INDEX_CACHE.set(Long.parseLong(index));
        }
    }

    public Directory getDirectory(String path) throws IOException {
        if (DIRECTORY_CACHE.containsKey(path)) {
            return DIRECTORY_CACHE.get(path);
        }
        Directory directory = FSDirectory.open(Paths.get(path));
        DIRECTORY_CACHE.put(path, directory);
        return directory;
    }

    public IndexWriter getWriter(Directory directory) throws IOException {
        if (WRITER_CACHE.containsKey(directory)) {
            IndexWriter indexWriter = WRITER_CACHE.get(directory);
            if (indexWriter.isOpen()) {
                return indexWriter;
            }
        }
        SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(directory, config);
        WRITER_CACHE.put(directory, indexWriter);
        return indexWriter;
    }

    public IndexReader getReader(Directory directory) throws IOException {
        if (READER_CACHE.containsKey(directory)) {
            IndexReader indexReader = READER_CACHE.get(directory);
            if (indexReader.getRefCount() > 0) {
                return indexReader;
            }
        }
        IndexReader indexReader = DirectoryReader.open(directory);
        READER_CACHE.put(directory, indexReader);
        return indexReader;
    }

    public Long getIdIndex() {
        long index = INDEX_CACHE.incrementAndGet();
        idOperations.set(LUCENE_ID_INDEX_KEY, String.valueOf(index));
        return index;
    }

}
