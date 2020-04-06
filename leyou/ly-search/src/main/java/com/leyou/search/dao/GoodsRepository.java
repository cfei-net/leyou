package com.leyou.search.dao;

import com.leyou.search.bo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * SpringDataElasticSearch ： 继承一个接口自动具备CRUD的方法
 */
public interface GoodsRepository extends ElasticsearchRepository<Goods, Long> {
}
