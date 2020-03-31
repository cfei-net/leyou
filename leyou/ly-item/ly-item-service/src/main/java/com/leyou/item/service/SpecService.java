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

import java.util.List;

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
}
