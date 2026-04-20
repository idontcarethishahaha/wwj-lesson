package org.example.es;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
/**
 * 类说明：
 *
 * @author WuWenJin
 * @version 1.0
 * @date 2026-04-19 15:23
 */
@Data
@Document(indexName = "ml-barrage")
public class BarrageDoc {
    @Id
    private String id;
    @Field(type = FieldType.Keyword)
    private String episodeId;
    @Field(type = FieldType.Keyword)
    private String text;
    @Field(type = FieldType.Keyword)
    private String color;
    @Field(type = FieldType.Keyword)
    private String time;
}

