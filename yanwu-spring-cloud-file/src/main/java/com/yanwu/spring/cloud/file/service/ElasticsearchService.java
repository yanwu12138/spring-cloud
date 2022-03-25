package com.yanwu.spring.cloud.file.service;

import com.yanwu.spring.cloud.file.pojo.elasticsearch.EsIndex;
import com.yanwu.spring.cloud.file.pojo.elasticsearch.EsType;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.cluster.metadata.MappingMetaData;

import java.util.Map;

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
    void indexCreate(EsIndex param) throws Exception;

    /**
     * 查询索引
     *
     * @param param 索引
     * @return 索引数据
     * @throws Exception .
     */
    Map<String, MappingMetaData> indexSelect(EsIndex param) throws Exception;

    /**
     * 删除索引
     *
     * @param param 索引
     * @throws Exception .
     */
    void indexDelete(EsIndex param) throws Exception;

    /**
     * 判断索引是否存在
     *
     * @param param 索引
     * @return 【true: 存在; false: 不存在】
     * @throws Exception .
     */
    boolean indexExists(EsIndex param) throws Exception;

    /**
     * 创建类型
     *
     * @param param 类型
     * @throws Exception .
     */
    void typeCreate(EsType<?> param) throws Exception;

    /**
     * 获取类型数据
     *
     * @param param 类型
     * @return 类型数据
     * @throws Exception .
     */
    GetResponse typeSelect(EsType<?> param) throws Exception;

    /**
     * 修改类型数据
     *
     * @param param 类型
     * @throws Exception .
     */
    void typeUpdate(EsType<?> param) throws Exception;

    /**
     * 删除类型
     *
     * @param param 类型
     * @throws Exception .
     */
    void typeDelete(EsType<?> param) throws Exception;

    /**
     * 判断类型是否存在
     *
     * @param param 类型
     * @return 【true: 存在; false: 不存在】
     * @throws Exception .
     */
    boolean typeExists(EsType<?> param) throws Exception;
}
