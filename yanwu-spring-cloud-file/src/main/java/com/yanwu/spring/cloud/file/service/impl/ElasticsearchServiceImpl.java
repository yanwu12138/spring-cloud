package com.yanwu.spring.cloud.file.service.impl;

import com.yanwu.spring.cloud.common.utils.JsonUtil;
import com.yanwu.spring.cloud.file.pojo.elasticsearch.BaseIndex;
import com.yanwu.spring.cloud.file.pojo.elasticsearch.BaseType;
import com.yanwu.spring.cloud.file.service.ElasticsearchService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Baofeng Xu
 * @date 2022/3/21 17:25.
 * <p>
 * description:
 */
@Slf4j
@Service
public class ElasticsearchServiceImpl implements ElasticsearchService {

    @Resource(name = "elasticsearchClient")
    private RestHighLevelClient elasticsearchClient;

    @Override
    public void indexCreate(BaseIndex<?> param) throws Exception {
        CreateIndexRequest indexRequest = new CreateIndexRequest(param.getIndex());
        CreateIndexResponse indexResponse = elasticsearchClient.indices().create(indexRequest, RequestOptions.DEFAULT);
        log.info("elasticsearch index create: {}", indexResponse);
    }

    @Override
    public boolean indexExists(BaseIndex<?> param) throws Exception {
        GetIndexRequest indexRequest = new GetIndexRequest(param.getIndex());
        boolean exists = elasticsearchClient.indices().exists(indexRequest, RequestOptions.DEFAULT);
        log.info("elasticsearch index exists, index: {}, exists: {}", param.getIndex(), exists);
        return exists;
    }

    @Override
    public void indexDelete(BaseIndex<?> param) throws Exception {
        DeleteIndexRequest indexRequest = new DeleteIndexRequest(param.getIndex());
        elasticsearchClient.indices().delete(indexRequest, RequestOptions.DEFAULT);
        log.info("elasticsearch index delete, index: {}", param.getIndex());
    }

    @Override
    public void typeAdd(BaseIndex<BaseType<?>> param) throws Exception {
        IndexRequest indexRequest = new IndexRequest(param.getIndex(), param.getType(), param.getTypeData().getTypeId());
        indexRequest.source(JsonUtil.toCompactJsonString(param.getTypeData().getData()), XContentType.JSON);
        IndexResponse indexResponse = elasticsearchClient.index(indexRequest, RequestOptions.DEFAULT);
        log.info("elasticsearch table add, param: {}, result: {}", param, indexResponse);
    }

    @Override
    public boolean typeExists(BaseIndex<BaseType<?>> param) throws Exception {
        GetRequest getRequest = new GetRequest(param.getIndex(), param.getType(), param.getTypeData().getTypeId());
        getRequest.fetchSourceContext(new FetchSourceContext(false));
        getRequest.storedFields("_none_");
        boolean exists = elasticsearchClient.exists(getRequest, RequestOptions.DEFAULT);
        log.info("elasticsearch table exists, param: {}, exists: {}", param, exists);
        return exists;
    }
}
