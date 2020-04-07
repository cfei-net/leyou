package com.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.utils.JsonUtils;
import com.leyou.common.vo.PageResult;
import com.leyou.item.client.ItemClient;
import com.leyou.item.dto.*;
import com.leyou.search.bo.Goods;
import com.leyou.search.dto.GoodsDTO;
import com.leyou.search.dto.SearchRequest;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private ItemClient itemClient;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    /**
     * 创建Goods对象
     * @param spuDTO    一个spu对应一个goods
     * @return          Goods
     */
    public Goods buildGoods(SpuDTO spuDTO){
        // 0、spu的ID
        Long spuId = spuDTO.getId();

        // 1、查询sku的集合
        List<SkuDTO> skuDTOList = itemClient.querySkuListBySpuId(spuId);
        List<Map<String, Object>> skuList = new ArrayList<>();
        for (SkuDTO skuDTO : skuDTOList) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", skuDTO.getId());
            map.put("image", StringUtils.substringBefore(skuDTO.getImages(), ","));
            map.put("price", skuDTO.getPrice());
            map.put("title", skuDTO.getTitle());
            skuList.add(map);
        }
        // 1.1 把对象转成json字符串
        String skuJsonStr = JsonUtils.toString(skuList);

        // 2、sku的价格集合: set有去重的功能，用于在页面搜索过滤
        Set<Long> priceSet = skuDTOList.stream().map(SkuDTO::getPrice).collect(Collectors.toSet());

        // 3、搜索字段：包含商品名称+分类名称+品牌名称
        // 3.1 分类名称
        List<CategoryDTO> categoryDTOList = itemClient.queryCategoryByIds(spuDTO.getCategoryIds());
        String categoryNames = categoryDTOList.stream().map(CategoryDTO::getName).collect(Collectors.joining());
        // 3.2 品牌名称
        BrandDTO brandDTO = itemClient.queryBrandById(spuDTO.getBrandId());
        // 3.3 拼接all字段
        String all = spuDTO.getName()+ categoryNames + brandDTO.getName();

        // 4、规格参数：Map结构
        // 4.1 准备一个map接收规格参数
        Map<String, Object> specs = new HashMap<>();
        // 4.2 查询出用于搜索过滤的规格参数
        List<SpecParamDTO> specParamDTOS = itemClient.querySpecParam(null, spuDTO.getCid3(), true);
        // 4.3 查询出商品详情
        SpuDetailDTO spuDetailDTO = itemClient.querySpuDetailById(spuId);
        // 4.4 通用规格参数
        String genericSpec = spuDetailDTO.getGenericSpec();
        Map<Long, Object> genericSpecMap = JsonUtils.toMap(genericSpec, Long.class, Object.class);
        // 4.5 特有规格参数
        String specialSpec = spuDetailDTO.getSpecialSpec();
        Map<Long, List<String>> specialSpecMap = JsonUtils.nativeRead(specialSpec, new TypeReference<Map<Long, List<String>>>() {
        });
        // 4.6 迭代规格参数
        for (SpecParamDTO param : specParamDTOS) {
            // 搜索过滤：规格参数的key
            String key = param.getName();
            // 获取值放入map，这个值有可能在通用规格中，也有可能在特有规格中
            Object value = null;
            if(param.getGeneric()){
                // 通用规格参数
                value = genericSpecMap.get(param.getId());
            }else{
                // 特有规格参数
                value = specialSpecMap.get(param.getId());
            }
            // 如果是数字，我们转成字符串的片段
            if(param.getNumeric()){
                value = chooseSegment(value, param);
            }

            // 放入规格参数map中
            specs.put(key, value);
        }

        // 5、实例化一个goods对象
        Goods goods = new Goods();
        goods.setId(spuId);
        goods.setSubTitle(spuDTO.getSubTitle());
        goods.setSkus(skuJsonStr); // 去查询skus列表转成json再设置进去
        goods.setPrice(priceSet); // 所有sku的价格集合
        goods.setAll(all); // all字段，这个字段用于搜索，包含商品名称+分类名称+品牌名称
        goods.setSpecs(specs);// 对应的所有的规格参数，用于过滤
        goods.setBrandId(spuDTO.getBrandId());
        goods.setCategoryId(spuDTO.getCid3());
        goods.setCreateTime(spuDTO.getCreateTime().getTime());

        // 6、返回
        return goods;
    }

    private String chooseSegment(Object value, SpecParamDTO p) {
        if (value == null || StringUtils.isBlank(value.toString())) {
            return "其它";
        }
        double val = parseDouble(value.toString());
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = parseDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = parseDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    private double parseDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 商品搜索
     * @param searchRequest     搜索条件
     * @return                  分页后的商品数据
     */
    public PageResult<GoodsDTO> search(SearchRequest searchRequest) {
        // 如果没有查询条件，我们抛异常
        String key = searchRequest.getKey();
        if(StringUtils.isBlank(key)){
            throw new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }

        // 原生的查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // ====================================================================================================
        // 1、控制字段的数量
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","subTitle","skus"}, null));

        // 2、拼接搜索条件:  分词查询
        queryBuilder.withQuery(QueryBuilders.matchQuery("all", key).operator(Operator.AND));

        // 3、分页条件设置
        Integer page = searchRequest.getPage() - 1;  // springdata的起始页是从0开始，所以减一
        Integer size = searchRequest.getSize();      // 页大小
        queryBuilder.withPageable(PageRequest.of(page, size));
        // ====================================================================================================
        // 执行查询操作
        AggregatedPage<Goods> goodsPage = elasticsearchTemplate.queryForPage(queryBuilder.build(), Goods.class);

        // 获取结果
        long totalElements = goodsPage.getTotalElements(); // 总记录数
        int totalPages = goodsPage.getTotalPages();     // 总页数
        List<Goods> goodsList = goodsPage.getContent(); //当前页数据

        // 返回数据
        return new PageResult<>(
                totalElements,
                totalPages,
                BeanHelper.copyWithCollection(goodsList, GoodsDTO.class));
    }
}
