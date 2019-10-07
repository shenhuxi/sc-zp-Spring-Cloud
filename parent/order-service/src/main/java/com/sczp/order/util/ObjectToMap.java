package com.sczp.order.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ObjectToMap {

     /**
      * 将对象转换成Map<String, String>格式
      *
      * @param obj
      * @return
      */
    public static Map<String, String> getNamValMap(Object obj, boolean isSort) {
        Map<String, String> map = null;
        if(isSort) {
            map = new TreeMap<String, String>();
        }else{
            map = new HashMap<String, String>();
        }
        Field[] fieldArr = obj.getClass().getDeclaredFields();
        try {
            for (Field field : fieldArr) {
                field.setAccessible(true);
                if (field.get(obj) != null && !"".equals(field.get(obj).toString())) {
                    map.put(field.getName(), field.get(obj).toString());
                }
            }
        } catch (IllegalAccessException e) {
            throw  new RuntimeException("转换错误");
        }
        return map;
    }
}
