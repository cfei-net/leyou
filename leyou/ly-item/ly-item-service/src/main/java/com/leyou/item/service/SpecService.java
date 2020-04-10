package com.leyou.item.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.item.dto.SpecGroupDTO;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.entity.SpecGroup;
import com.leyou.item.entity.SpecParam;
import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SpecService {

    @Autowired
    private SpecGroupMapper specGroupMapper;

    @Autowired
    private SpecParamMapper specParamMapper;

    /**
     * 查询规格组
     * @param cid
     * @return
     */
    public List<SpecGroupDTO> querySpecGroupByCategoryId(Long cid) {
        // 查询
        SpecGroup record = new SpecGroup();
        record.setCid(cid);
        List<SpecGroup> specList = specGroupMapper.select(record);
        // 空的校验
        if(CollectionUtils.isEmpty(specList)){
            throw new LyException(ExceptionEnum.SPEC_NOT_FOUND);
        }
        // 返回
        return BeanHelper.copyWithCollection(specList, SpecGroupDTO.class);
    }

    /**
     * 查询规格参数
     * @param gid
     * @param cid
     * @param searching
     * @return
     */
    public List<SpecParamDTO> querySpecParam(Long gid, Long cid, Boolean searching) {
        // 严谨性校验： 规格组id和分类id最少有一个
        if(gid ==null && cid == null){
            throw new LyException(ExceptionEnum.SPEC_NOT_FOUND);
        }
        // 拼接条件
        SpecParam record = new SpecParam();
        record.setGroupId(gid);
        record.setCid(cid);
        record.setSearching(searching);
        List<SpecParam> specParams = specParamMapper.select(record);
        // 空的校验
        if(CollectionUtils.isEmpty(specParams)){
            throw new LyException(ExceptionEnum.SPEC_NOT_FOUND);
        }
        // 返回
        return BeanHelper.copyWithCollection(specParams, SpecParamDTO.class);
    }

    /**
     * 根据分类id查询规格组和组内参数
     * @param categoryId
     * @return
     */
    public List<SpecGroupDTO> querySpecGroupAndParamsByCategoryId(Long categoryId) {
        // 1、查询规格组
        List<SpecGroupDTO> groupDTOList = querySpecGroupByCategoryId(categoryId);
        // 2、查询组内参数
        // 第三种方式： 可以优化里面的第二重for循环
        List<SpecParamDTO> params = querySpecParam(null, categoryId, null);
        // 把所有规格参数数据转成map
        Map<Long, List<SpecParamDTO>> paramMap = params.stream().collect(Collectors.groupingBy(SpecParamDTO::getGroupId));
        // 把规格参数放入规格组中
        for (SpecGroupDTO specGroupDTO : groupDTOList) {
            specGroupDTO.setParams(paramMap.get(specGroupDTO.getId()));
        }


        /*Map<Long, List<SpecParamDTO>> map = new HashMap<>();
        for (SpecParamDTO param : params) {
            Long groupId = param.getGroupId();
            if(CollectionUtils.isEmpty(map.get(groupId))){
                ArrayList<SpecParamDTO> list = new ArrayList<SpecParamDTO>();
                map.put(groupId, list);
            }
            map.get(groupId).add(param);
        }
        for (SpecGroupDTO specGroupDTO : groupDTOList) {
            specGroupDTO.setParams(map.get(specGroupDTO.getId()));
        }*/


        /*
        双重for循环：效率不高
        // 第二种方式：根据分类id 一次性查询出所有的规格参数，然后在代码中再去设置到组内
        List<SpecParamDTO> params = querySpecParam(null, categoryId, null);
        for (SpecGroupDTO group : groupDTOList) {
            // 判断如果组内的规格参数集合为空，实例化一个集合给他
            if(CollectionUtils.isEmpty(group.getParams())){
                group.setParams(new ArrayList<SpecParamDTO>());
            }
            for (SpecParamDTO param : params) {
                // 如果规格组id和参数的组id一致
                if(param.getGroupId() == group.getId()){
                    group.getParams().add(param);
                }
            }
        }*/


        /*
        第一种方式： 问题在for循环中操作数据库，性能不好
        for (SpecGroupDTO specGroupDTO : groupDTOList) {
            List<SpecParamDTO> params = querySpecParam(specGroupDTO.getId(), null, null);
            specGroupDTO.setParams(params);
        }*/
        // 3、返回
        return groupDTOList;
    }
}
