package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.BrandDTO;
import com.leyou.item.dto.CategoryDTO;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.entity.Brand;
import com.leyou.item.entity.Spu;
import com.leyou.item.mapper.SpuDetailMapper;
import com.leyou.item.mapper.SpuMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsService {

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    /**
     * 分页查询商品列表
     * @param key
     * @param page
     * @param rows
     * @param saleable
     * @return
     */
    public PageResult<SpuDTO> querySpuListByPage(String key, Integer page, Integer rows, Boolean saleable) {
        // 1、设置分页参数
        PageHelper.startPage(page, rows);
        // 2、条件查询
        // 2.1 拼接条件
        Example example = new Example(Spu.class);  // from  tb_spu
        Example.Criteria criteria = example.createCriteria();
        // 2.2 key判断
        if(StringUtils.isNotBlank(key)){
            criteria.andLike("name", "%"+key+"%");  // where name like ?
        }
        // 2.3 是否上下架
        if(saleable != null){
            criteria.andEqualTo("saleable", saleable);  // and saleable = ?

        }
        // 2.4 排序
        example.setOrderByClause("update_time DESC");  // order by update_time DESC
        // 2.5查询结果
        List<Spu> spuList = spuMapper.selectByExample(example);
        // 3、判空
        if(CollectionUtils.isEmpty(spuList)){
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        // 4、封装结果
        // 4.1 把list转成分页结果
        PageInfo<Spu> pageInfo = new PageInfo<>(spuList);
        // 4.2 获取结果
        List<SpuDTO> spuDTOList = BeanHelper.copyWithCollection(pageInfo.getList(), SpuDTO.class);

        // 4.3 处理返回的对象的： 分类名称和品牌名称
        handlerCategoryAndBrandName(spuDTOList);

        // 5、返回结果
        return new PageResult<>(pageInfo.getTotal(), pageInfo.getPages(), spuDTOList);
    }

    /**
     * 给spuDto添加分类的名称和品牌名称
     * @param spuDTOList
     */
    private void handlerCategoryAndBrandName(List<SpuDTO> spuDTOList) {
        for (SpuDTO spuDTO : spuDTOList) {
            // 处理品牌名称
            Long brandId = spuDTO.getBrandId();
            BrandDTO brand = brandService.queryBrandById(brandId);
            spuDTO.setBrandName(brand.getName());

            // 处理分类名称
            List<CategoryDTO> categoryDTOList = categoryService.queryCategoryByIds(spuDTO.getCategoryIds());
            // 使用流的方式去获取分类的名称
            String cName = categoryDTOList.stream().map(CategoryDTO::getName).collect(Collectors.joining("/"));
            spuDTO.setCategoryName(cName);
        }
    }
}
