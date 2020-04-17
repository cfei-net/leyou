package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.constants.MQConstants;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.*;
import com.leyou.item.entity.Brand;
import com.leyou.item.entity.Sku;
import com.leyou.item.entity.Spu;
import com.leyou.item.entity.SpuDetail;
import com.leyou.item.mapper.SkuMapper;
import com.leyou.item.mapper.SpuDetailMapper;
import com.leyou.item.mapper.SpuMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.leyou.common.constants.MQConstants.Exchange.ITEM_EXCHANGE_NAME;
import static com.leyou.common.constants.MQConstants.RoutingKey.*;

@Slf4j
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

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private AmqpTemplate amqpTemplate; // 发消息

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

    /**
     * 保存商品
     * @param spuDTO
     */
    @Transactional
    public void saveGoods(SpuDTO spuDTO) {
        // 1、保存spu
        // 1.1 把dto转成spu
        Spu spu = BeanHelper.copyProperties(spuDTO, Spu.class);
        spu.setSaleable(false); // 新增的商品都是下架的
        int count = spuMapper.insertSelective(spu);
        if(count != 1){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
        Long spuId = spu.getId();
        log.info("【商品微服务】保存商品：返回的id：{}",spuId);
        // 2、保存SpuDetail
        SpuDetailDTO spuDetailDTO = spuDTO.getSpuDetail();
        spuDetailDTO.setSpuId(spuId); // 把spu的id设置进来
        SpuDetail spuDetail = BeanHelper.copyProperties(spuDetailDTO, SpuDetail.class);
        count = spuDetailMapper.insertSelective(spuDetail);
        if(count != 1){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
        // 3、保存Sku列表：批量插入
        List<SkuDTO> skus = spuDTO.getSkus();
        List<Sku> skuList = BeanHelper.copyWithCollection(skus, Sku.class);
        skuList.forEach(sku -> {
            sku.setSpuId(spuId); // 设置spu的id
        });
        count = skuMapper.insertList(skuList);
        if(count != skuList.size()){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
    }

    /**
     * 更新商品的上下架
     * @param spuId
     * @param saleable
     */
    @Transactional
    public void updateGoodsSaleable(Long spuId, Boolean saleable) {
        // 1、更新spu的 ： saleable
        Spu spu = new Spu();
        spu.setId(spuId);
        spu.setSaleable(saleable);// 要么上架；要么下架
        int count = spuMapper.updateByPrimaryKeySelective(spu);// 根据主键更新非空字段
        if(count != 1 ){
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }

        // 2、更新spu对应所有sku： enable
        // 2.1 修改什么内容
        Sku sku = new Sku();
        sku.setEnable(saleable);  // update tb_sku set enable = ?
        // 2.2 修改的条件是什么
        Example example = new Example(Sku.class);
        example.createCriteria().andEqualTo("spuId", spuId); // where spu_id = ?
        count = skuMapper.updateByExampleSelective(sku, example);
        if(count < 1 ){
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }

        // 3、上下架的时候给RabbitMQ发一条消息：去做商品的静态页和es索引数据的更新
        amqpTemplate.convertAndSend(
                ITEM_EXCHANGE_NAME,
                (saleable ? ITEM_UP_KEY : ITEM_DOWN_KEY),
                spuId
                );
    }

    /**
     * 根据id查询商品详情
     * @param spuId
     * @return
     */
    public SpuDetailDTO querySpuDetailById(Long spuId) {
        SpuDetail spuDetail = spuDetailMapper.selectByPrimaryKey(spuId);
        if(spuDetail == null){
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        return BeanHelper.copyProperties(spuDetail, SpuDetailDTO.class);
    }

    /**
     * 查询sku列表
     * @param spuId
     * @return
     */
    public List<SkuDTO> querySkuListBySpuId(Long spuId) {
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        List<Sku> skuList = skuMapper.select(sku);
        if(CollectionUtils.isEmpty(skuList)){
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        return BeanHelper.copyWithCollection(skuList, SkuDTO.class);
    }

    /**
     * 更新商品数据
     * @param spuDTO
     */
    @Transactional
    public void updateGoods(SpuDTO spuDTO) {
        // 0、校验
        Long spuId = spuDTO.getId();
        if(spuId == null){
            throw new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }
        // 1、更新spu
        Spu spu = BeanHelper.copyProperties(spuDTO, Spu.class);
        spu.setSaleable(null); // 因为上下架操作，我们有单独的一个功能，不应该在修改的时候去做
        spu.setUpdateTime(new Date());
        int count = spuMapper.updateByPrimaryKeySelective(spu);
        if(count != 1){
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }
        // 2、更新spuDetail
        SpuDetailDTO spuDetailDTO = spuDTO.getSpuDetail();
        SpuDetail spuDetail = BeanHelper.copyProperties(spuDetailDTO, SpuDetail.class);
        spuDetail.setSpuId(spuId);// 设置一下id
        spuDetail.setUpdateTime(new Date());
        count = spuDetailMapper.updateByPrimaryKeySelective(spuDetail);
        if(count != 1){
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }

        // 3、先删除Sku
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        // 总记录数据
        int size = skuMapper.selectCount(sku);
        if(size > 0){
            // 删除条数
            count = skuMapper.delete(sku);
            if(size != count){
                throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
            }
        }

        // 4、再插入sku列表
        List<SkuDTO> skus = spuDTO.getSkus();
        List<Sku> skuList = BeanHelper.copyWithCollection(skus, Sku.class);
        skuList.forEach(insertSku -> {
            insertSku.setSpuId(spuId); // 设置spu的id
        });
        count = skuMapper.insertList(skuList);
        if(count != skuList.size()){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
    }

    /**
     * 根据spu的id去查询spu
     * @param id
     * @return
     */
    public SpuDTO querySpuById(Long id) {
        // 查询商品
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if(spu==null){
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        // 换成dto
        SpuDTO spuDTO = BeanHelper.copyProperties(spu, SpuDTO.class);
        // 查询详情
        spuDTO.setSpuDetail(querySpuDetailById(id));
        // 查sku集合
        spuDTO.setSkus(querySkuListBySpuId(id));
        // 返回
        return spuDTO;
    }

    /**
     * 根据skuid集合去查询sku
     * @param ids
     * @return
     */
    public List<SkuDTO> querySkuListByIds(List<Long> ids) {
        /*if(ids.size()> 99){
            throw new LyException(ExceptionEnum.INVALID_CARTS_NUM_ERROR);
        }*/
        List<Sku> skuList = skuMapper.selectByIdList(ids);
        if(CollectionUtils.isEmpty(skuList)){
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        return BeanHelper.copyWithCollection(skuList,SkuDTO.class);
    }
}
