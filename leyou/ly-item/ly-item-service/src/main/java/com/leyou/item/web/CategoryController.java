package com.leyou.item.web;

import com.leyou.item.dto.CategoryDTO;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// 第一种：解决跨域的方案：  每一个Controller都要加
/*@CrossOrigin(
        value={"http://manage.leyou.com","http://www.leyou.com"},  //允许那个域名跨域
        allowedHeaders = "*",  // 允许哪些头： * 代表所有
        allowCredentials = "true"  // 允许携带cookie
)*/
@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    /**
     * 根据父id查询分类信息
     * @param pid       父id
     * @return          返回dto
     */
    @GetMapping("/category/of/parent")
    public ResponseEntity<List<CategoryDTO>> queryCategoryByParentId(@RequestParam("pid") Long pid){
        List<CategoryDTO> categoryDTOList = categoryService.queryCategoryByParentId(pid);
        return ResponseEntity.ok(categoryDTOList);
    }

    /**
     * 通过分类id的集合查询分类的信息
     * @param ids       分类的id集合
     * @return          分类的DTO
     */
    @GetMapping("/category/list")
    public ResponseEntity<List<CategoryDTO>> queryCategoryByIds(@RequestParam("ids") List<Long> ids){
        List<CategoryDTO> categoryDTOList = categoryService.queryCategoryByIds(ids);
        return ResponseEntity.ok(categoryDTOList);
    }
}
