package com.leyou.item.client;

import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "item-service")
public interface ItemClient {

    /**
     * 分页查询商品列表
     * @param key           查询条件
     * @param page          当前页
     * @param rows          页大小
     * @param saleable      是否上下架
     * @return              spuDTO分页对象
     */
    @GetMapping("/spu/page")
    public PageResult<SpuDTO> querySpuListByPage(
            @RequestParam(value="key", required = false) String key,
            @RequestParam(value="page", defaultValue = "1") Integer page,
            @RequestParam(value="rows", defaultValue = "10") Integer rows,
            @RequestParam(value="saleable", required = false) Boolean saleable
    );


    /**
     * 根据spu的id查询sku集合
     * @param spuId     spu的id
     * @return          sku的：列表
     */
    @GetMapping("/sku/of/spu")
    public List<SkuDTO> querySkuListBySpuId(@RequestParam("id") Long spuId);

    /**
     * 根据id查询商品详情
     * @param spuId     spu的id
     * @return          spuDetail
     */
    @GetMapping("/spu/detail")
    public SpuDetailDTO querySpuDetailById(@RequestParam("id") Long spuId);

    /**
     * 通过分类id的集合查询分类的信息
     * @param ids       分类的id集合
     * @return          分类的DTO
     */
    @GetMapping("/category/list")
    public List<CategoryDTO> queryCategoryByIds(@RequestParam("ids") List<Long> ids);


    /**
     * 通过品牌id查询品牌的信息
     * @param id    品牌的id
     * @return      品牌的DTO
     */
    @GetMapping("/brand/{id}")
    public BrandDTO queryBrandById(@PathVariable("id") Long id);


    /**
     * 查询规格参数
     * @param gid           规格组id
     * @param cid           分类id
     * @param searching     是否搜索
     * @return
     */
    @GetMapping("/spec/params")
    public List<SpecParamDTO> querySpecParam(
            @RequestParam(value="gid",required = false) Long gid,
            @RequestParam(value="cid",required = false) Long cid,
            @RequestParam(value="searching",required = false) Boolean searching
    );

    /**
     * 批量查询品牌
     * @param brandIds
     * @return
     */
    @GetMapping("/brand/list")
    public List<BrandDTO> queryBrandByIds(@RequestParam("ids") List<Long> brandIds);
}
