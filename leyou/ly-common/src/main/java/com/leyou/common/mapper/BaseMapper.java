package com.leyou.common.mapper;

import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.common.IdsMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * 我们自己定义的基础Mapper，每个工程的mapper都可以继承它
 * @param <T>
 */
public interface BaseMapper<T> extends Mapper<T>, IdsMapper<T>, IdListMapper<T,Long> {
}
