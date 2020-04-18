package com.leyou.order.mapper;

import com.leyou.common.mapper.BaseMapper;
import com.leyou.order.entity.OrderDetail;
import tk.mybatis.mapper.additional.insert.InsertListMapper;

/**
 * InsertListMapper:  批量插入订单条目，id不是自增长
 */
public interface OrderDetailMapper extends BaseMapper<OrderDetail>, InsertListMapper<OrderDetail> {
}
