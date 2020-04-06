package com.leyou.item.web;

import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.BrandDTO;
import com.leyou.item.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Autowired
    private BrandService brandService;

    /**
     * 根据条件分页查询品牌列表
     * @param key       查询条件
     * @param page      当前页
     * @param rows      页大小
     * @param sortBy    排序字段名称
     * @param desc      是否降序
     * @return          分页结果对象： vo
     */
    @GetMapping("/page")
    public ResponseEntity<PageResult<BrandDTO>> queryBrandListByPage(
            @RequestParam(value="key", required = false) String key,
            @RequestParam(value="page", defaultValue = "1") Integer page,
            @RequestParam(value="rows", defaultValue = "10") Integer rows,
            @RequestParam(value="sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value="desc", defaultValue = "false") Boolean desc
    ){
        // 调用Service查询
        PageResult<BrandDTO> pageResult = brandService.queryBrandListByPage(key, page, rows, sortBy, desc);
        // 返回给前端
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 新增品牌
     * @param brandDTO      Dto接收参数
     * @param cids          分类Id集合
     * @return              无
     */
    @PostMapping
    public ResponseEntity<Void> saveBrand(BrandDTO brandDTO, @RequestParam("cids") List<Long> cids){
        brandService.saveBrand(brandDTO, cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    /**
     * 根据分类的id查询品牌信息
     * @param categoryId    分类id
     * @return              品牌集合
     */
    @GetMapping("/of/category")
    public ResponseEntity<List<BrandDTO>> queryBrandByCategoryId(@RequestParam("id") Long categoryId){
        List<BrandDTO> brandDTOList = brandService.queryBrandByCategoryId(categoryId);
        return ResponseEntity.ok(brandDTOList);
    }


    /**
     * 通过品牌id查询品牌的信息
     * @param id    品牌的id
     * @return      品牌的DTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<BrandDTO> queryBrandById(@PathVariable("id") Long id){
        BrandDTO brandDTO = brandService.queryBrandById(id);
        return ResponseEntity.ok(brandDTO);
    }

}
