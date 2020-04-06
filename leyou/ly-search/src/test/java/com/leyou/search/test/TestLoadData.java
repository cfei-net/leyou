package com.leyou.search.test;

import com.leyou.LySearchApplication;
import com.leyou.common.vo.PageResult;
import com.leyou.item.client.ItemClient;
import com.leyou.item.dto.SpuDTO;
import com.leyou.search.bo.Goods;
import com.leyou.search.dao.GoodsRepository;
import com.leyou.search.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LySearchApplication.class)
public class TestLoadData {

    @Autowired
    private SearchService searchService;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private ItemClient itemClient;


    @Test
    public void test(){

        int page = 1, rows = 100;
        do {
            try {
                // 分页查询spu集合
                PageResult<SpuDTO> result = itemClient.querySpuListByPage(null, page, rows, true);
                // 获取item
                List<SpuDTO> items = result.getItems();

                // 构建Goods集合
                List<Goods> goodsList = items.stream().map(searchService::buildGoods).collect(Collectors.toList());
                // 插入索引库
                goodsRepository.saveAll(goodsList);
                // 分页
                page++;
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }while (true);
    }

}
