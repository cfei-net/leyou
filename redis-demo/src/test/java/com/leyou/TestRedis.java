package com.leyou;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestRedis {

    /**
     * 细节：
     *      1、spring给我们提供一个redis的模板：RedisTemplate
     *                  这个模板：采用的jdk的序列化方式存储key和value
     *
     *      2、Spring还给我们提供了一个字符串的Redis的模板： StringRedisTemplate
     *                  这个模板： 专门采用字符串【json】的方式去序列化key和value
     *
     */
    /*@Autowired
    private RedisTemplate redisTemplate;*/

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void test(){
        // 存入数据
        /**
         * 参数三： 数字，存入缓存的有效期
         * 参数四： 单位
         */
        redisTemplate.opsForValue().set("name","小野鸡" , 20, TimeUnit.SECONDS);

        // 取出数据
        String name = (String) redisTemplate.opsForValue().get("name");
        System.out.println("从redis中取出数据name："+name);
    }
}
