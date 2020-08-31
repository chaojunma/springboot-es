package com.mk.controller;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mk.entity.Item;
import com.mk.service.EsCurdService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/es")
public class IndexController {

    @Autowired
    private EsCurdService esCurdService;

    // es索引(相当于数据库)
    public static final String ES_INDEX_NAME = "es-index";

    // es文档类型(相当于库表)
    public static final String ES_DOC_TYPE = "item";

    @GetMapping("/add")
    public void create(){
        Item item = Item.builder()
                .id(1L)
                .title("小米手机7")
                .category("手机")
                .brand("小米")
                .images("http://image.leyou.com/13123.jpg")
                .build();
        XContentBuilder builder = Item.buildMapping();
        esCurdService.createIndex(ES_INDEX_NAME, ES_DOC_TYPE, builder);
        esCurdService.insert(ES_INDEX_NAME, ES_DOC_TYPE, item);
    }


    @GetMapping("/update")
    public void update(){
        Item item = Item.builder()
                .id(1L)
                .title("小米手机8")
                .category("手机")
                .brand("小米")
                .images("http://image.leyou.com/13123.jpg")
                .build();

        String _id = "RsBwNHQBWruqdhIY3dgZ";

        esCurdService.update(ES_INDEX_NAME, ES_DOC_TYPE, _id, item);
    }


    @GetMapping("/list")
    public List<Item> list(){
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 分页参数
        sourceBuilder.from(0);
        sourceBuilder.size(10);

        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        // 关键词查找匹配，这个不是精确查找，他会对字段中的内容进行切词匹配
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("title", "小米");
        boolBuilder.must(matchQueryBuilder);
        // 设置需要返回的字段
        sourceBuilder.fetchSource(new String[]{"id", "title", "category"}, new String[]{}).query(boolBuilder);

        // 高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<span style=\"color:red\">"); //前置元素
        highlightBuilder.postTags("</span>");  //后置元素
        highlightBuilder.fields().add(new HighlightBuilder.Field("title"));  //高亮查询字段
        highlightBuilder.requireFieldMatch(false);     //如果要多个字段高亮,这项要为false
        sourceBuilder.highlighter(highlightBuilder);

        SearchResponse response =  esCurdService.search(ES_INDEX_NAME, ES_DOC_TYPE, sourceBuilder);


        ObjectMapper objectMapper = new ObjectMapper();
        List<Item> result = new ArrayList<>();

        response.getHits().iterator().forEachRemaining(hit -> {
            Map<String, HighlightField> highlightFields  = hit.getHighlightFields();
            HighlightField content = highlightFields.get("title");
            //fragments就是高亮显示的片段
            Text[] fragments = content.fragments();
            StringBuffer sb = new StringBuffer();
            //拼接查询出来的高亮片段
            for (Text text:fragments){
                sb.append(text);
            }

            //将查询结果转化为对象--因为我们要进行替换，所以要转化成对象，然后直接将高亮片段替换原有对象内容
            try {
                Item item = objectMapper.readValue(hit.getSourceAsString() , Item.class);
                // 设置ES中主键
                item.set_id(hit.getId());

                //开始进行高亮替换
                if(StringUtils.isNotEmpty(sb.toString())) {
                    item.setTitle(sb.toString());
                }
                result.add(item);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        });

        return result;
    }
}
