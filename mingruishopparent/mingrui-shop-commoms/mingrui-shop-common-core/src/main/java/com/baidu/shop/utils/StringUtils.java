package com.baidu.shop.utils;

public class StringUtils {

    public static Boolean notNull(String str){
        return str==null?false:true;
    }

    public static Boolean isEmpty(String str){
        return (str==null || str.equals(""))?true:false;
    }
}
