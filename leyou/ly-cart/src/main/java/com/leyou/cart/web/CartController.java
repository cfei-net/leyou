package com.leyou.cart.web;

import com.leyou.cart.entity.Cart;
import com.leyou.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 把商品添加到购物车
     * @param cart  购物车对象
     * @return      没有返回值
     */
    @PostMapping
    public ResponseEntity<Void> addCart(@RequestBody Cart cart){
        cartService.addCart(cart);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 查询用户的购物车信息
     *      根据用户的id来查询，用户ID使用UserHolder来获取
     * @return      购物车列表
     */
    @GetMapping("/list")
    public ResponseEntity<List<Cart>> queryCartList(){
        List<Cart> cartList =  cartService.queryCartList();
        return ResponseEntity.ok(cartList);
    }
}
