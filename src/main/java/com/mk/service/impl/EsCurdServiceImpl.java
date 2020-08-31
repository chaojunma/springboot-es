package com.mk.service.impl;

import com.alibaba.fastjson.JSON;
import com.mk.service.EsCurdService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Slf4j
@Service
public class EsCurdServiceImpl implements EsCurdService {

    @Value("${elasticsearch.index.number-of-shards:3}")
    private int number_of_shards;

    @Value("${elasticsearch.index.number-of-replicas:2}")
    private int number_of_replicas;

    @Autowired
    private RestHighLevelClient highLevelClient;

    @Override
    public void createIndex(String indexName) {
        // 判断是否存在索引
        if(existIndex(indexName)) {
            return;
        }

        // 创建索引
        CreateIndexRequest createIndexRequest = new CreateIndexRequest();
        createIndexRequest.index(indexName).settings(Settings.builder()
                // 分片数
                .put("index.number_of_shards", number_of_shards)
                // 备份数
                .put("index.number_of_replicas", number_of_replicas));
        CreateIndexResponse response = null;
        try {
            response = highLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.info(e.getMessage(), e);
        }
    }

    @Override
    public void createIndex(String indexName, String type, XContentBuilder builder) {
        // 判断是否存在索引
        if(existIndex(indexName)) {
            return;
        }

        // 创建索引
        CreateIndexRequest createIndexRequest = new CreateIndexRequest();
        createIndexRequest.index(indexName).settings(Settings.builder()
                // 分片数
                .put("index.number_of_shards", number_of_shards)
                // 备份数
                .put("index.number_of_replicas", number_of_replicas))
                // 创建映射
                .mapping(type, builder);
        CreateIndexResponse response = null;
        try {
            response = highLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.info(e.getMessage(), e);
        }
    }

    @Override
    public boolean existIndex(String indexName) {
        // 判断索引是否存在
        boolean isExist = false;
        GetIndexRequest getIndexRequest = new GetIndexRequest();
        getIndexRequest.indices(indexName);
        try {
            isExist = highLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.info(e.getMessage(), e);
        }
        return isExist;
    }



    @Override
    public void deleteIndex(String indexName) {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest();
        deleteIndexRequest.indices(indexName);
        try {
            highLevelClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.info(e.getMessage(), e);
        }
    }


    @Override
    public RestStatus insert(String indexName, String type, Object object) {
        IndexRequest indexRequest = new IndexRequest();
        // source方法里面需要填写对应数据的类型，默认数据类型为json
        indexRequest.index(indexName).type(type).source(JSON.toJSONString(object), XContentType.JSON);
        IndexResponse response = null;
        try {
            response = highLevelClient.index(indexRequest, RequestOptions.DEFAULT);
            return response.status();
        } catch (IOException e) {
            log.info(e.getMessage(), e);
        }

        return null;
    }


    @Override
    public RestStatus update(String indexName, String type, String id, Object object) {
        UpdateRequest request = new UpdateRequest();
        request.index(indexName).type(type).id(id).doc(JSON.toJSONString(object), XContentType.JSON);
        UpdateResponse response = null;
        try {
            response = highLevelClient.update(request, RequestOptions.DEFAULT);
            return response.status();
        } catch (IOException e) {
            log.info(e.getMessage(), e);
        }
        return null;
    }


    @Override
    public RestStatus delete(String indexName, String type, String id) {
        DeleteRequest request = new DeleteRequest();
        request.index(indexName).type(type).id(id);
        DeleteResponse response = null;
        try {
            response = highLevelClient.delete(request, RequestOptions.DEFAULT);
            return response.status();
        } catch (IOException e) {
            log.info(e.getMessage(), e);
        }
        return null;
    }



    @Override
    public SearchResponse search(String indexName, String type, SearchSourceBuilder sourceBuilder) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(indexName).types(type).source(sourceBuilder);
        SearchResponse response = null;
        try {
            response = highLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            return response;
        } catch (IOException e) {
            log.info(e.getMessage(), e);
        }
        return null;
    }
}
