package com.leyou.item.mapper;

import com.leyou.common.mapper.BaseMapper;
import com.leyou.item.entity.Sku;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.special.InsertListMapper;

public interface SkuMapper extends BaseMapper<Sku>, InsertListMapper<Sku> {

    /**
     * 减库存操作
     * @param skuId     sku的id
     * @param num       减去的数量
     * @return          返回更新条数
     */
    @Update("UPDATE tb_sku SET stock = stock - #{num} WHERE id = #{skuId}")
    int minusStock(@Param("skuId") Long skuId, @Param("num") Integer num);
}
