package com.yanwu.spring.cloud.file.service.impl;

import com.yanwu.spring.cloud.common.utils.JsonUtil;
import com.yanwu.spring.cloud.file.pojo.elasticsearch.*;
import com.yanwu.spring.cloud.file.service.ElasticsearchService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.client.RequestOptions.DEFAULT;

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
        CreateIndexResponse response = elasticsearchClient.indices().create(request, DEFAULT);
        log.info("elasticsearch index create, param: {}, result: {}", param, response);
    }

    @Override
    public Map<String, MappingMetaData> indexSelect(EsIndex param) throws Exception {
        GetIndexRequest request = new GetIndexRequest(param.getIndex());
        GetIndexResponse response = elasticsearchClient.indices().get(request, DEFAULT);
        log.info("elasticsearch index select, param: {}, result: {}", param, response);
        return response.getMappings();
    }

    @Override
    public void indexDelete(EsIndex param) throws Exception {
        DeleteIndexRequest request = new DeleteIndexRequest(param.getIndex());
        elasticsearchClient.indices().delete(request, DEFAULT);
        log.info("elasticsearch index delete, param: {}", param);
    }

    @Override
    public boolean indexExists(EsIndex param) throws Exception {
        GetIndexRequest request = new GetIndexRequest(param.getIndex());
        boolean exists = elasticsearchClient.indices().exists(request, DEFAULT);
        log.info("elasticsearch index exists, index: {}, result: {}", param.getIndex(), exists);
        return exists;
    }

    @Override
    public void typeCreate(EsType<?> param) throws Exception {
        IndexRequest request = new IndexRequest(param.getIndex().getIndex(), param.getType(), param.getTypeId());
        request.source(JsonUtil.toCompactJsonString(param.getData()), XContentType.JSON);
        IndexResponse response = elasticsearchClient.index(request, DEFAULT);
        log.info("elasticsearch type create, param: {}, result: {}", param, response);
    }

    @Override
    public void typeBulkCreate(List<EsType<?>> param) throws Exception {
        BulkRequest request = new BulkRequest();
        for (EsType<?> type : param) {
            IndexRequest index = new IndexRequest(type.getIndex().getIndex(), type.getType(), type.getTypeId());
            request.add(index.source(JsonUtil.toCompactJsonString(type.getData()), XContentType.JSON));
        }
        BulkResponse response = elasticsearchClient.bulk(request, DEFAULT);
        log.info("elasticsearch type bulk create, param: {}, result: {}", param, response);
    }

    @Override
    public GetResponse typeSelect(EsType<?> param) throws Exception {
        GetRequest request = new GetRequest(param.getIndex().getIndex(), param.getType(), param.getTypeId());
        GetResponse response = elasticsearchClient.get(request, DEFAULT);
        log.info("elasticsearch type select, param: {}, result: {}", param, response);
        return response;
    }

    @Override
    public void typeUpdate(EsType<?> param) throws Exception {
        UpdateRequest request = new UpdateRequest(param.getIndex().getIndex(), param.getType(), param.getTypeId());
        request.doc(JsonUtil.toCompactJsonString(param.getData()), XContentType.JSON);
        UpdateResponse response = elasticsearchClient.update(request, DEFAULT);
        log.info("elasticsearch type update, param: {}, result: {}", param, response);
    }

    @Override
    public void typeBulkUpdate(List<EsType<?>> param) throws Exception {
        BulkRequest request = new BulkRequest();
        for (EsType<?> type : param) {
            UpdateRequest update = new UpdateRequest(type.getIndex().getIndex(), type.getType(), type.getTypeId());
            request.add(update.doc(JsonUtil.toCompactJsonString(type.getData()), XContentType.JSON));
        }
        BulkResponse response = elasticsearchClient.bulk(request, DEFAULT);
        log.info("elasticsearch type bulk update, param: {}, result: {}", param, response);
    }

    @Override
    public void typeDelete(EsType<?> param) throws Exception {
        DeleteRequest request = new DeleteRequest(param.getIndex().getIndex(), param.getType(), param.getTypeId());
        DeleteResponse response = elasticsearchClient.delete(request, DEFAULT);
        log.info("elasticsearch type select, param: {}, result: {}", param, response);
    }

    @Override
    public void typeBulkDelete(List<EsType<?>> param) throws Exception {
        BulkRequest request = new BulkRequest();
        for (EsType<?> type : param) {
            DeleteRequest delete = new DeleteRequest(type.getIndex().getIndex(), type.getType(), type.getTypeId());
            request.add(delete);
        }
        BulkResponse response = elasticsearchClient.bulk(request, DEFAULT);
        log.info("elasticsearch type bulk delete, param: {}, result: {}", param, response);
    }

    @Override
    public boolean typeExists(EsType<?> param) throws Exception {
        GetRequest request = new GetRequest(param.getIndex().getIndex(), param.getType(), param.getTypeId());
        request.fetchSourceContext(new FetchSourceContext(false));
        request.storedFields(NONE);
        boolean exists = elasticsearchClient.exists(request, DEFAULT);
        log.info("elasticsearch type exists, param: {}, result: {}", param, exists);
        return exists;
    }

    @Override
    public List<EsTypeData> typeSearch(EsSearch param) throws Exception {
        SearchRequest request = new SearchRequest(param.getType().getIndex().getIndex());
        request.types(param.getType().getType());
        // ***** 组装查询参数
        request.source(param.searchBuilder(TestType.class));
        SearchResponse response = elasticsearchClient.search(request, DEFAULT);
        if (response.getHits().getHits() == null || response.getHits().getHits().length <= 0) {
            return Collections.emptyList();
        }
        List<EsTypeData> result = new ArrayList<>();
        for (SearchHit searchHit : response.getHits().getHits()) {
            result.add(JsonUtil.toObject(searchHit.getSourceAsString(), TestType.class));
        }
        log.info("elasticsearch type search, param: {}, result: {}", param, result);
        return result;
    }

}
