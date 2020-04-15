package com.leyou.auth.mapper;

import com.leyou.auth.entity.ApplicationInfo;
import com.leyou.common.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ApplicationInfoMapper extends BaseMapper<ApplicationInfo> {
    /**
     * 根据服务id去查询目标id
     * @param serviceId 当前服务的id
     * @return  目标ID的集合
     */
    List<Long> queryTargetIdListByServiceId(@Param("id") Long serviceId);
}
