/**
 *
 */
package com.sczp.order.jpa.utils;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;

public class ReflectionUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ReflectionUtils.class);

    /**
     * 构造私有
     */
    private ReflectionUtils() {}

    /**
     * 转换字符串类型到clazz的property类型的值.
     * @param value  待转换的字符串
     * @param toType 提供类型信息的Class
     */
    public static Object convertValue(Object value, Class<?> toType) {
        try {
            if (Date.class.isAssignableFrom(toType)) {
                DateConverter dc = new DateConverter();
                dc.setUseLocaleFormat(true);
                dc.setPatterns(new String[] { "yyyy", "yyyy-MM", "yyyy-MM-dd", "yyyy-MM-dd HH", "yyyy-MM-dd HH:mm", "yyyy-MM-dd HH:mm:ss", "yyyy/MM", "yyyy/MM/dd", "yyyy/MM/dd HH", "yyyy/MM/dd HH:mm", "yyyy/MM/dd HH:mm:ss", "yyyy年MM月", "yyyy年MM月dd日", "yyyy年MM月dd日HH时", "yyyy年MM月dd日HH时mm分", "yyyy年MM月dd日HH时mm分ss秒" });
                ConvertUtils.register(dc, Date.class);
            }
            return ConvertUtils.convert(value, toType);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return value;
        }
    }
    
    /**
     * 获取属性名
     * @param clazz
     * @return
     */
    @SuppressWarnings("rawtypes")
	public static String[] getFieldNameArray(Class clazz) {
    	Field[] fields = clazz.getDeclaredFields();
    	if(fields != null && fields.length >0 ) {
    		String[] attrs = new String[fields.length];
    		for (int i= 0;i<fields.length; i++) {
    			attrs[i] = fields[i].getName();
    		}
    		return attrs;
    	}
		return new String[0];
    }
    
    /**
	 * 获取对象里不为空的值
	 * @param obj
	 * @param map
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Map<String, Object> getNotNullFieldToMap(Object obj, Map<String, Object> map){
		try {
			Class clazz = Class.forName(obj.getClass().getName());
			Field[] fields = clazz.getDeclaredFields();
			for (Field field:fields) {
				field.setAccessible(true);
				String name = field.getName();
				String bname = name.substring(0, 1).toUpperCase() + name.substring(1);
				Method m = obj.getClass().getMethod("get" + bname);
				// 调用getter方法获取属性值
				Object value = m.invoke(obj);
				if(value != null){
					map.put("EQ_" + name, value);
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
		} 
		return map;
	}
	

}
