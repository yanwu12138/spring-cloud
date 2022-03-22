package com.yanwu.spring.cloud.file.service;

import com.yanwu.spring.cloud.file.pojo.elasticsearch.BaseIndex;
import com.yanwu.spring.cloud.file.pojo.elasticsearch.BaseType;

/**
 * @author Baofeng Xu
 * @date 2022/3/21 17:24.
 * <p>
 * description:
 */
public interface ElasticsearchService {

    /**
     * 创建索引
     *
     * @param param 索引
     * @throws Exception .
     */
    void indexCreate(BaseIndex<?> param) throws Exception;

    /**
     * 判断索引是否存在
     *
     * @param param 索引
     * @return 【true: 存在; false: 不存在】
     * @throws Exception .
     */
    boolean indexExists(BaseIndex<?> param) throws Exception;

    /**
     * 删除索引
     *
     * @param param 索引
     * @throws Exception .
     */
    void indexDelete(BaseIndex<?> param) throws Exception;

    /**
     * 创建类型
     *
     * @param param 类型
     * @throws Exception .
     */
    void typeAdd(BaseIndex<BaseType<?>> param) throws Exception;

    /**
     * 判断类型是否存在
     *
     * @param param 类型
     * @return 【true: 存在; false: 不存在】
     * @throws Exception .
     */
    boolean typeExists(BaseIndex<BaseType<?>> param) throws Exception;
}
