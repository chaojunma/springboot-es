package com.mk.entity;


import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import java.io.IOException;

@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    // ES主键
    private String _id;

    // 数据库主键
    private Long id;

    //标题
    private String title;

    // 分类
    private String category;

    // 品牌
    private String brand;

    // 价格
    private Double price;

    // 图片地址
    private String images;


    public static XContentBuilder buildMapping (){
        XContentBuilder builder = null;
        try {
            builder = JsonXContent.contentBuilder()
                    .startObject()
                        .startObject("properties")
                            .startObject("id")
                            .field("type", "long")
                            .field("index", "true")
                            .endObject()

                            .startObject("title")
                            .field("type", "text")
                            .field("index", "true")
                            .endObject()

                            .startObject("category")
                            .field("type", "keyword")
                            .field("index", "true")
                            .endObject()

                            .startObject("brand")
                            .field("type", "keyword")
                            .field("index", "true")
                            .endObject()

                            .startObject("price")
                            .field("type", "double")
                            .field("index", "true")
                            .endObject()

                            .startObject("images")
                            .field("type", "keyword")
                            .field("index", "true")
                            .endObject()
                        .endObject()
                    .endObject();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return builder;
    }
}
