package com.leyou.search.bo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Map;
import java.util.Set;

/**
 * 一个SPU【`tb_spu`】对应一个Goods
 */
@Data
@Document(
        indexName = "goods",    // 索引名称
        type = "docs",          // 类型名称
        shards = 1,             // 分片数量
        replicas = 1            // 副本
)
public class Goods {
    @Id
    @Field(type = FieldType.Keyword)
    private Long id; // spuId
    @Field(type = FieldType.Keyword, index = false)
    private String subTitle;// 卖点
    @Field(type = FieldType.Keyword, index = false)
    private String skus;// sku信息的json结构

    @Field(
            type = FieldType.Text,   // 会分词
            analyzer = "ik_max_word" // 指定分词器
    )
    private String all; // 所有需要被搜索的信息，包含标题，分类，甚至品牌
    private Long brandId;// 品牌id
    private Long categoryId;// 商品3级分类id
    private Long createTime;// spu创建时间
    private Set<Long> price;// 价格
    private Map<String, Object> specs;// 可搜索的规格参数，key是参数名，值是参数值
    /**
     *
     * 动态模板约束这些  ：  不确定的map中的key
     *
     * es 底层会把map结构的类型转成字段：
     *          specs.CPU品牌       骁龙（Snapdragon)     使用动态模板约束这些字段都是keyword：不分词
     *          specs.内存          3GB
     *          specs.操作系统
     *          specs.屏幕分辨率
     *          。。。。
     */

}
