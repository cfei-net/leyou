package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.BrandDTO;
import com.leyou.item.entity.Brand;
import com.leyou.item.mapper.BrandMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 品牌Service
 */
@Service
public class BrandService {

    @Autowired
    private BrandMapper brandMapper;

    /**
     * 分页查询品牌列表
     * @param key
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @return
     */
    public PageResult<BrandDTO> queryBrandListByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc) {
        // 1、 分页参数设置
        PageHelper.startPage(page, rows);
        // 2、 分页条件查询
        // 2.1 封装条件： select * from tb_brand
        Example example = new Example(Brand.class);
        // 2.2 拼接查询条件 ：  where name like ?  or  letter = ?
        if(StringUtils.isNotBlank(key)){
            example.createCriteria()
                    .orLike("name", "%"+key+"%")
                    .orEqualTo("letter", key);
        }
        // 2.3 排序:  order by id desc|asc
        example.setOrderByClause(sortBy + " " + (desc?"DESC":"ASC"));
        // 2.4 查询
        List<Brand> brands = brandMapper.selectByExample(example);

        // 3、 判断结果
        if(CollectionUtils.isEmpty(brands)){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }

        // 4、 转成返回的分页结果对象
        // 4.1 我们要获取到分页助手帮我们封装好的分页数据
        PageInfo<Brand> pageInfo = new PageInfo<>(brands);
        // 4.2 获取数据
        long total = pageInfo.getTotal();
        int totalPage = pageInfo.getPages();
        List<Brand> brandList = pageInfo.getList();
        // 4.3 转成PageResult
        PageResult<BrandDTO> pageResult = new PageResult<>(
                total,
                totalPage,
                BeanHelper.copyWithCollection(brandList, BrandDTO.class)
        );

        // 5、 返回
        return pageResult;
    }

    /**
     * 新增品牌
     * @param brandDTO
     * @param cids
     */
    @Transactional  // 事务保证：两个插入要么一起成功；要么一起失败
    public void saveBrand(BrandDTO brandDTO, List<Long> cids) {
        // 1、新增品牌
        // 1.1 拷贝属性： 参数一： 数据来源    参数二： 转成什么对象
        Brand brand = BeanHelper.copyProperties(brandDTO, Brand.class);
        // 1.2 保存:  只要保存成功，品牌对象就能获取到数据库返回的自增长的id：  @KeySql(useGeneratedKeys = true)
        int count = brandMapper.insertSelective(brand);
        // 1.3 判断
        if(count != 1){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
        // 品牌id
        Long bId = brand.getId();

        // 2、新增品牌和分类的中间表
        count = brandMapper.insertCategoryBrand(bId, cids);
        if(count!=cids.size()){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }

    }
}
