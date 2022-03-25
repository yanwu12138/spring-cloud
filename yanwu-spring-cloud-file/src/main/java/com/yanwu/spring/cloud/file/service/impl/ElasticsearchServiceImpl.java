package com.yanwu.spring.cloud.file.service.impl;

import com.yanwu.spring.cloud.common.utils.JsonUtil;
import com.yanwu.spring.cloud.file.pojo.elasticsearch.EsIndex;
import com.yanwu.spring.cloud.file.pojo.elasticsearch.EsType;
import com.yanwu.spring.cloud.file.service.ElasticsearchService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
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
    public void indexCreate(EsIndex param) throws Exception {
        CreateIndexRequest request = new CreateIndexRequest(param.getIndex());
        CreateIndexResponse response = elasticsearchClient.indices().create(request, RequestOptions.DEFAULT);
        log.info("elasticsearch index create: {}", response);
    }

    @Override
    public boolean indexExists(EsIndex param) throws Exception {
        GetIndexRequest request = new GetIndexRequest(param.getIndex());
        boolean exists = elasticsearchClient.indices().exists(request, RequestOptions.DEFAULT);
        log.info("elasticsearch index exists, index: {}, exists: {}", param.getIndex(), exists);
        return exists;
    }

    @Override
    public void indexDelete(EsIndex param) throws Exception {
        DeleteIndexRequest request = new DeleteIndexRequest(param.getIndex());
        elasticsearchClient.indices().delete(request, RequestOptions.DEFAULT);
        log.info("elasticsearch index delete, index: {}", param.getIndex());
    }

    @Override
    public void typeCreate(EsType<?> param) throws Exception {
        IndexRequest request = new IndexRequest(param.getIndex().getIndex(), param.getType(), param.getTypeId());
        request.source(JsonUtil.toCompactJsonString(param.getData()), XContentType.JSON);
        IndexResponse response = elasticsearchClient.index(request, RequestOptions.DEFAULT);
        log.info("elasticsearch type create, param: {}, result: {}", param, response);
    }

    @Override
    public boolean typeExists(EsType<?> param) throws Exception {
        GetRequest request = new GetRequest(param.getIndex().getIndex(), param.getType(), param.getTypeId());
        request.fetchSourceContext(new FetchSourceContext(false));
        request.storedFields("_none_");
        boolean exists = elasticsearchClient.exists(request, RequestOptions.DEFAULT);
        log.info("elasticsearch type exists, param: {}, exists: {}", param, exists);
        return exists;
    }

    @Override
    public String typeSelect(EsType<?> param) throws Exception {
        GetRequest request = new GetRequest(param.getIndex().getIndex(), param.getType(), param.getTypeId());
        GetResponse response = elasticsearchClient.get(request, RequestOptions.DEFAULT);
        log.info("elasticsearch type select, param: {}, result: {}", param, response);
        return JsonUtil.toCompactJsonString(response);
    }

    @Override
    public void typeUpdate(EsType<?> param) throws Exception {
        UpdateRequest request = new UpdateRequest(param.getIndex().getIndex(), param.getType(), param.getTypeId());
        request.doc(JsonUtil.toCompactJsonString(param.getData()), XContentType.JSON);
        UpdateResponse response = elasticsearchClient.update(request, RequestOptions.DEFAULT);
        log.info("elasticsearch type update, param: {}, result: {}", param, response);
    }

    @Override
    public void typeDelete(EsType<?> param) throws Exception {
        DeleteRequest request = new DeleteRequest(param.getIndex().getIndex(), param.getType(), param.getTypeId());
        DeleteResponse response = elasticsearchClient.delete(request, RequestOptions.DEFAULT);
        log.info("elasticsearch type select, param: {}, result: {}", param, response);
    }


}
