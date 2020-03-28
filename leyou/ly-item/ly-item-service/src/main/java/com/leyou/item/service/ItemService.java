package com.leyou.item.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.entity.Item;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class ItemService {

    public Item saveItem(Item item){
        // 如果名称为空，则抛出异常，返回400状态码，请求参数有误
        if(item.getName() == null){
            // 状态码： 随意写的； 报错内容也是随意写的
            // 如果与前端交互的时候，对方会把一些状态码或者提示信息写到前端代码，如果我们随意的话，前端可能出问题
            // 我们的状态码和提示信息，应该和前端人员沟通好，定好格式
            //throw new LyException(401,"名称不能为空");
            //  使用枚举类来定义异常信息
            throw new LyException(ExceptionEnum.NAME_CAN_NOT_BE_NULL);
        }
        int id = new Random().nextInt(100);
        item.setId(id);
        return item;
    }
}