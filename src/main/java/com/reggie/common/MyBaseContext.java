package com.reggie.common;

//基于threadlocal工具类，用来设置和获取当前登录用户ID
public class MyBaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
