package com.leyou.item.web;

import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.BrandDTO;
import com.leyou.item.dto.SkuDTO;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.dto.SpuDetailDTO;
import com.leyou.item.service.GoodsService;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GoodsController {

    @Autowired
    private GoodsService goodsService;


    /**
     * 分页查询商品列表
     * @param key           查询条件
     * @param page          当前页
     * @param rows          页大小
     * @param saleable      是否上下架
     * @return              spuDTO分页对象
     */
    @GetMapping("/spu/page")
    public ResponseEntity<PageResult<SpuDTO>> querySpuListByPage(
            @RequestParam(value="key", required = false) String key,
            @RequestParam(value="page", defaultValue = "1") Integer page,
            @RequestParam(value="rows", defaultValue = "10") Integer rows,
            @RequestParam(value="saleable", required = false) Boolean saleable
    ){
        // 查询商品列表
        PageResult<SpuDTO> pageResult = goodsService.querySpuListByPage(key, page, rows, saleable);
        // 返回给前端
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 保存商品数据
     * @param spuDTO       接收页面传入的数据： 包括商品、商品详情、包括sku列表
     * @return             没有返回值
     */
    @PostMapping("/goods")
    public ResponseEntity<Void> saveGoods(@RequestBody SpuDTO spuDTO){
        goodsService.saveGoods(spuDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 更新商品的上下架
     * @param spuId     spu的id
     * @param saleable  是否上下架
     * @return          没有返回值
     */
    @PutMapping("/spu/saleable")
    public ResponseEntity<Void> updateGoodsSaleable(
            @RequestParam("id") Long spuId,
            @RequestParam(value = "saleable",required = true)Boolean saleable
            ){
        // 更新
        goodsService.updateGoodsSaleable(spuId, saleable);
        // 返回
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 根据id查询商品详情
     * @param spuId     spu的id
     * @return          spuDetail
     */
    @GetMapping("/spu/detail")
    public ResponseEntity<SpuDetailDTO> querySpuDetailById(@RequestParam("id") Long spuId){
        SpuDetailDTO spuDetailDTO = goodsService.querySpuDetailById(spuId);
        return ResponseEntity.ok(spuDetailDTO);
    }

    /**
     * 根据spu的id查询sku集合
     * @param spuId     spu的id
     * @return          sku的：列表
     */
    @GetMapping("/sku/of/spu")
    public ResponseEntity<List<SkuDTO>> querySkuListBySpuId(@RequestParam("id") Long spuId){
        List<SkuDTO> skuList = goodsService.querySkuListBySpuId(spuId);
        return ResponseEntity.ok(skuList);
    }

    /**
     * 更新商品数据
     * @param spuDTO    spu的dto接收页面传入的参数
     * @return          没有返回值
     */
    @PutMapping("/goods")
    public ResponseEntity<Void> updateGoods(@RequestBody SpuDTO spuDTO){
        goodsService.updateGoods(spuDTO);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 根据spuid查询spu信息
     * @param id    spu的id
     * @return
     */
    @GetMapping("/spu/{id}")
    public ResponseEntity<SpuDTO> querySpuById(@PathVariable("id") Long id){
        SpuDTO spuDTO = goodsService.querySpuById(id);
        return ResponseEntity.ok(spuDTO);
    }

}
