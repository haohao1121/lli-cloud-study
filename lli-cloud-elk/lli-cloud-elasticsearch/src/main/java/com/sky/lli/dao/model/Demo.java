package com.sky.lli.dao.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author lihao (15215401693@163.com)
 * @date 2020/12/21
 */

@Data
@Document(indexName = "demo.es", type = "_doc", shards = 1, replicas = 0)
public class Demo {

    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String firstCode;

    @Field(type = FieldType.Keyword)
    private String secordCode;

    @Field(type = FieldType.Text)
    private String content;

    @Field(type = FieldType.Integer)
    private Integer type;
}
