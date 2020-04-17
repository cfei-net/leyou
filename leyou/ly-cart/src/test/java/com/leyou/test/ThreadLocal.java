package com.leyou.test;

import java.util.concurrent.ConcurrentHashMap;
/*

public class ThreadLocal<T> {

    // 线程安全的Map： 在多线程环境下不会出现并发问题
    private ConcurrentHashMap<Thread, T> TL = new ConcurrentHashMap();

    public void set(T t){
        TL.put(Thread.currentThread(), t);
    }

    public T get(){
        return TL.get(Thread.currentThread());
    }
}
*/
