package com.leyou.item.service;

import com.leyou.common.exception.LyException;
import com.leyou.item.entity.Item;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class ItemService {

    public Item saveItem(Item item){
        // 如果名称为空，则抛出异常，返回400状态码，请求参数有误
        if(item.getName() == null){
            throw new LyException(401,"名称不能为空");
        }
        int id = new Random().nextInt(100);
        item.setId(id);
        return item;
    }
}