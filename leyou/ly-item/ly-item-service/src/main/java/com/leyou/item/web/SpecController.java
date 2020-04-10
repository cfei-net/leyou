package com.leyou.item.web;

import com.leyou.item.dto.SpecGroupDTO;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.entity.SpecParam;
import com.leyou.item.service.SpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/spec")
public class SpecController {

    @Autowired
    private SpecService specService;

    /**
     * 根据分类的id查询规格组信息
     * @param cid   分类id
     * @return      规格组列表
     */
    @GetMapping("/groups/of/category")
    public ResponseEntity<List<SpecGroupDTO>> querySpecGroupByCategoryId(@RequestParam("id") Long cid){
        List<SpecGroupDTO> specGroupList = specService.querySpecGroupByCategoryId(cid);
        return ResponseEntity.ok(specGroupList);
    }

    /**
     * 查询规格参数
     * @param gid           规格组id
     * @param cid           分类id
     * @param searching     是否搜索
     * @return
     */
    @GetMapping("/params")
    public ResponseEntity<List<SpecParamDTO>> querySpecParam(
            @RequestParam(value="gid",required = false) Long gid,
            @RequestParam(value="cid",required = false) Long cid,
            @RequestParam(value="searching",required = false) Boolean searching
    ){
        List<SpecParamDTO> specParamList = specService.querySpecParam(gid, cid, searching);
        return ResponseEntity.ok(specParamList);
    }

    /**
     * 根据分类id查询规格组和组内参数
     * @param categoryId    分类id
     * @return              规格组和组内参数
     */
    @GetMapping("/of/category")
    public ResponseEntity<List<SpecGroupDTO>> querySpecGroupAndParamsByCategoryId(@RequestParam("id") Long categoryId){
        List<SpecGroupDTO> specGroupDTOList = specService.querySpecGroupAndParamsByCategoryId(categoryId);
        return ResponseEntity.ok(specGroupDTOList);
    }
}
