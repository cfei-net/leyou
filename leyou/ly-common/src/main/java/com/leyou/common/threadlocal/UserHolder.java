package com.leyou.common.threadlocal;

import com.leyou.common.auth.entity.UserInfo;

/**
 * 用这个工具类来获取当前用户信息
 */
public class UserHolder {
    // 使用这个对象来封装当前的用户
    private final static ThreadLocal<UserInfo> TL = new ThreadLocal<>();

    public static void setUser(UserInfo userInfo) {
        TL.set(userInfo);
    }

    public static UserInfo getUser(){
        return TL.get();
    }

    public static  void removeUser(){
        TL.remove();
    }
}
