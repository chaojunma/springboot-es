package com.mk.service;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.builder.SearchSourceBuilder;

public interface EsCurdService {

    /**
     * 创建索引
     * @param indexName 索引名称
     * @return
     */
    public void createIndex(String indexName);

    /**
     * 创建索引
     * @param indexName 索引名称
     * @param type      数据对象类型，可以把它设置成对象类的类名
     * @param builder
     */
    public void createIndex(String indexName, String type, XContentBuilder builder);


    /**
     * 判断索引是否存在
     * @param indexName
     * @return
     */
    public boolean existIndex(String indexName);


    /**
     * 删除索引
     * @param indexName 索引名称
     */
    public void deleteIndex(String indexName);


    /**
     * 新增数据
     * @param indexName 索引名称
     * @param type      数据对象类型，可以把它设置成对象类的类名
     * @param object    数据对象
     * @return
     */
    public RestStatus insert(String indexName, String type, Object object);

    /**
     *
     * 修改数据
     * @param indexName  索引名称
     * @param type       数据对象类型，可以把它设置成对象类的类名
     * @param id        主键
     * @param object    数据对象
     * @return
     */
    public RestStatus update(String indexName, String type, String id, Object object);


    /**
     * 删除数据
     * @param indexName 索引名称
     * @param type      数据对象类型，可以把它设置成对象类的类名
     * @param id        主键 RsBwNHQBWruqdhIY3dgZ
     * @return
     */
    public RestStatus delete(String indexName, String type, String id);


    /**
     * 数据查询
     * @param indexName
     * @param type
     * @param builder
     * @return
     */
    public SearchResponse search(String indexName, String type, SearchSourceBuilder builder);
}
