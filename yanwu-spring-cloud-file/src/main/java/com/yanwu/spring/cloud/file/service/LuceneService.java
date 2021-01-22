package com.yanwu.spring.cloud.file.service;

import com.yanwu.spring.cloud.common.pojo.PageParam;
import com.yanwu.spring.cloud.file.pojo.LuceneDocument;
import com.yanwu.spring.cloud.file.pojo.LuceneSearch;

import java.util.List;

/**
 * @author Baofeng Xu
 * @date 2021/1/22 15:49.
 * <p>
 * description:
 */
public interface LuceneService {

    /**
     * 创建索引
     *
     * @param param 文档内容
     * @return 索引ID
     * @throws Exception Exception.class
     */
    Long create(LuceneDocument param) throws Exception;

    /**
     * 根据条件删除索引
     *
     * @param field field
     * @param value value
     * @return 删除的索引ID
     * @throws Exception Exception.class
     */
    Long delete(String field, String value) throws Exception;

    /**
     * 更新索引
     *
     * @param id    索引ID
     * @param field field
     * @param value value
     * @throws Exception Exception.class
     */
    void update(Long id, String field, String value) throws Exception;

    /**
     * 根据条件查询所有的索引
     *
     * @param param 查询条件
     * @return 文档集合
     * @throws Exception Exception.class
     */
    List<LuceneDocument> searchAll(LuceneSearch param) throws Exception;

    /**
     * 根据条件分页查询索引
     *
     * @param param 查询条件
     * @return 文档集合
     * @throws Exception Exception.class
     */
    List<LuceneDocument> pageSearch(PageParam<LuceneSearch> param) throws Exception;
}
