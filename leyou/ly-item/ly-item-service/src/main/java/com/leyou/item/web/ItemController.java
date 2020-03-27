package com.leyou.item.web;

import com.leyou.item.entity.Item;
import com.leyou.item.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
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
                .status(201)// 指定状态码
                .body(i); // 指定返回体
    }
}
