package com.leyou.item.mapper;

import com.leyou.item.entity.Brand;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandMapper extends Mapper<Brand> {
    /**
     * 插入分类和品牌的中间表
     * @param bId   品牌id
     * @param cids  分类的id集合
     * @return      返回插入的条数
     */
    int insertCategoryBrand(@Param("bId") Long bId, @Param("ids") List<Long> cids);
}
