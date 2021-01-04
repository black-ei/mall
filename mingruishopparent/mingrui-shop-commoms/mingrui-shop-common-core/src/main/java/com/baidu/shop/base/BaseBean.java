package com.baidu.shop.base;

import org.springframework.beans.BeanUtils;

public class BaseBean {

    public static <T> T copyProperties(Object object,Class<T> clazz){

        if(null == object) return null;
        if(null == clazz) return null;
        try {
            T t = clazz.newInstance();
            BeanUtils.copyProperties(object,t);
            return t;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
