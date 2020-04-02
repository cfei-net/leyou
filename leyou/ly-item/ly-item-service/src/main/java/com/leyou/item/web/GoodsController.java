package com.leyou.item.web;

import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.BrandDTO;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

}
