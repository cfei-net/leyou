package com.leyou.cart.service;

import com.leyou.cart.entity.Cart;
import com.leyou.common.auth.entity.UserInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.threadlocal.UserHolder;
import com.leyou.common.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    // 购物车前缀
    private static final String KEY_PREFIX = "ly:cart:uid:";

    /**
     * 添加商品到购物车
     * @param cart
     */
    public void addCart(Cart cart) {
        // 1、获取当前线程上的用户信息
        UserInfo user = UserHolder.getUser();
        //log.info("【购物车微服务】获取当前线程上的用户信息：user = {}", user);
        // 2、从redis中取出购物车
        // 2.1 拼接key
        String key = KEY_PREFIX + user.getId();
        // 2.2 第一层key，取出购物车
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(key);
        // 3、判断商品是否在购物车中
        // 3.1 取出skuId : 转成字符串即可  第二层key
        String skuId = cart.getSkuId().toString();
        // 3.2 判断商品是否在redis中
        Boolean bool = hashOps.hasKey(skuId);
        // 3.3 商品数量
        Integer num = cart.getNum(); // 页面传过来的
        if(bool!=null && bool){
            // 4、如果存在，修改数量
            cart = JsonUtils.toBean(hashOps.get(skuId), Cart.class);
            // 4.1 修改数量
            cart.setNum(cart.getNum() + num);
        }
        // 5、再把购物车信息写回redis中
        hashOps.put(skuId, JsonUtils.toString(cart));
        log.info("【购物车微服务】添加购物车信息成功：当前用户信息： {} ， 商品信息： {}", user, cart);
    }

    /**
     * 查询购物车信息
     * @return
     */
    public List<Cart> queryCartList() {
        // 1、获取当前登录用户
        UserInfo user = UserHolder.getUser();
        log.info("【购物车微服务】获取购物车信息成功：当前用户信息： {} ", user);
        // 2、判断是否有购物车
        String key = KEY_PREFIX + user.getId();
        Boolean bool = redisTemplate.hasKey(key);
        if(bool == null || !bool){
            throw new LyException(ExceptionEnum.CARTS_NOT_FOUND);
        }
        // 3、查询购物车，并且判断有无数据
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(key);
        // 3.1 判断有几条数据
        Long size = hashOps.size();
        if(size == null || size < 0){
            throw new LyException(ExceptionEnum.CARTS_NOT_FOUND);
        }
        // 4、把购物车信息取出来，都是String，我们要把字符串转成对象
        List<String> carts = hashOps.values();
        //hashOps.keys();//获取所有的key
        // 5、转成对象并返回
        return carts.stream().map(json -> JsonUtils.toBean(json, Cart.class)).collect(Collectors.toList());
    }
}
