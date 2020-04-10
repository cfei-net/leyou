package com.leyou.page.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.client.ItemClient;
import com.leyou.item.dto.BrandDTO;
import com.leyou.item.dto.CategoryDTO;
import com.leyou.item.dto.SpecGroupDTO;
import com.leyou.item.dto.SpuDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PageService {

    @Autowired
    private ItemClient itemClient;

    /**
     * 根据spu的id去加载商品详情页的所有的数据
     * @param spuId
     * @return
     */
    public Map<String, Object> loadItemData(Long spuId) {
        //==============================================
        // 请求到达之前，我们先去缓存找：redis
        //==============================================


        // 查询spu
        SpuDTO spuDTO = itemClient.querySpuById(spuId);
        // 查询分类信息
        List<CategoryDTO> categoryDTOList = itemClient.queryCategoryByIds(spuDTO.getCategoryIds());
        // 品牌信息
        BrandDTO brandDTO = itemClient.queryBrandById(spuDTO.getBrandId());
        // 查询规格组和组内参数
        List<SpecGroupDTO> groupDTOList = itemClient.querySpecGroupAndParamsByCategoryId(spuDTO.getCid3());

        Map<String, Object> data = new HashMap<>();
        data.put("categories", categoryDTOList);  // 分类信息
        data.put("brand", brandDTO); // 品牌信息
        data.put("spuName", spuDTO.getName()); // 商品名称
        data.put("subTitle", spuDTO.getSubTitle()); // 买点
        data.put("detail", spuDTO.getSpuDetail()); // 商品详情对象
        data.put("skus", spuDTO.getSkus()); // sku的集合
        data.put("specs", groupDTOList); // 规格过滤参数: 规格组套规格参数

        //========================================
        // 可以把整个map存入redis中
        //========================================


        return data;
    }


    @Autowired
    private SpringTemplateEngine templateEngine;

    @Value("${ly.static.itemDir}")
    private String itemDir;

    @Value("${ly.static.templateName}")
    private String templateName;

    /**
     * 生成静态化页面
     */
    public void createItemHtml(Long spuId){
        // 1、准备上下文对象
        Context context = new Context();
        // 1.1 根据spuid查询出商品详情所有的数据
        context.setVariables(loadItemData(spuId));
        // 2、生成目录
        File dir = new File(itemDir);
        if(!dir.exists()){
            if(!dir.mkdir()){
                throw new LyException(ExceptionEnum.DIRECTORY_WRITER_ERROR);
            }
        }
        // 3、创建文件
        File file = new File(dir, spuId + ".html");
        // 4、生成静态化页面
        try(PrintWriter writer = new PrintWriter(file,"UTF-8")){
            templateEngine.process(templateName,context, writer);
        }catch (Exception e){
            log.error("【静态页服务】生成静态页失败：{}",e.getMessage());
            throw new LyException(ExceptionEnum.FILE_WRITER_ERROR);
        }
    }



}
