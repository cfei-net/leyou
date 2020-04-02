package com.leyou.item.mapper;

import com.leyou.item.entity.Brand;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
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

    /**
     * 根据分类id查询品牌
     * @param categoryId
     * @return
     */
    @Select("SELECT b.* FROM tb_category_brand t INNER JOIN tb_brand b ON b.`id` = t.`brand_id` WHERE t.`category_id` = #{cid}")
    List<Brand> queryBrandByCategoryId(@Param("cid") Long categoryId);
}
