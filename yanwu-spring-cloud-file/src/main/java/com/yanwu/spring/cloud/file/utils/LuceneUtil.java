package com.yanwu.spring.cloud.file.utils;

import com.yanwu.spring.cloud.file.pojo.LuceneDocument;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

import java.util.Map;
import java.util.Objects;

/**
 * @author Baofeng Xu
 * @date 2021/1/21 15:31.
 * <p>
 * description: lucene相关工具
 */
public class LuceneUtil {

    public static Document createDocument(Long id, LuceneDocument param) {
        Document document = new Document();
        // ----- 添加字段,这里字段的参数：字段的名称、字段的值、是否存储。Store.YES存储，Store.NO是不存储
        document.add(new StringField("id", String.valueOf(id), Field.Store.YES));
        document.add(new TextField("title", param.getTitle(), Field.Store.YES));
        document.add(new TextField("content", param.getContent(), Field.Store.YES));
        if (Objects.nonNull(param.getJson())) {
            document(param.getJson().getInnerMap(), document);
        }
        return document;
    }

    private static void document(Map<String, Object> map, Document document) {
        map.forEach((key, value) -> {
            if (value instanceof Map) {
                document((Map<String, Object>) value, document);
            } else {
                document.add(new TextField(key, String.valueOf(value), Field.Store.YES));
            }
        });
    }

}
