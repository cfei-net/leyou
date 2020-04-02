package com.leyou.item.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.item.dto.CategoryDTO;
import com.leyou.item.entity.Category;
import com.leyou.item.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 查询分类信息
     * @param pid   根据父id
     * @return
     */
    public List<CategoryDTO> queryCategoryByParentId(Long pid) {
        Category record = new Category();
        record.setParentId(pid);
        List<Category> categoryList = categoryMapper.select(record);
        // 处理异常
        if(CollectionUtils.isEmpty(categoryList)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        // 第一种转法
        /*List<CategoryDTO> cList = new ArrayList<>();
        for (Category c : categoryList) {
            CategoryDTO cd = new CategoryDTO();
            cd.setId(c.getId());
            cd.setName(c.getName());
            //...
            //...
            cList.add(cd);
        }*/
        // 第二种
        List<CategoryDTO> cList = BeanHelper.copyWithCollection(categoryList, CategoryDTO.class);
        return cList;
    }

    /**
     * 根据分类id集合去批量查询分类信息
     * @param categoryIds   分类的ID集合
     * @return              返回分类的列表
     *
     * 扩展通用mapper
     */
    public List<CategoryDTO> queryCategoryByIds(List<Long> categoryIds) {
        List<Category> categoryList = categoryMapper.selectByIdList(categoryIds);
        if (CollectionUtils.isEmpty(categoryList)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        return BeanHelper.copyWithCollection(categoryList, CategoryDTO.class);
    }
}
