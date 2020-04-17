package com.leyou.test;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ManyUserThead {

    // 线程安全的Map： 在多线程环境下不会出现并发问题
    //private ConcurrentHashMap TL = new ConcurrentHashMap();

    private ThreadLocal<String> TL = new ThreadLocal<>();

    public void test(){
        // 多线程环境演示
        for (int i=0; i<5; i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //随机生成一个用户id
                    String userId = RandomStringUtils.randomNumeric(6);
                    // 把当前线程作为key，用户id作为value存入
                    TL.set(userId);
                    // 获取当前线程下的用户的信息
                    System.out.println("当前线程："+Thread.currentThread().getName()+"========>当前用户id="+TL.get());
                    System.out.println("当前线程："+Thread.currentThread().getName()+"========>当前用户id="+TL.get());
                    System.out.println("当前线程："+Thread.currentThread().getName()+"========>当前用户id="+TL.get());
                }
            }).start();
        }
    }

    public static void main(String[] args) {
        new ManyUserThead().test();
    }
}
