package com.leyou.item.web;

import com.leyou.item.entity.Item;
import com.leyou.item.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ItemController {

    @Autowired
    private ItemService itemService;

    @PostMapping("/item")
    public ResponseEntity<Item> saveItem(Item item){
        Item i = itemService.saveItem(item);
        return ResponseEntity
                .status(HttpStatus.CREATED)// 指定状态码: spring提供了一个状态码的类，我们可以用它： HttpStatus
                .body(i); // 指定返回体
    }


}
