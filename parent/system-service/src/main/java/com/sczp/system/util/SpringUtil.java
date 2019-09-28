package com.sczp.system.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 *
 * SpringUtil 用来取SpringContext
 * @author zp
 */
@Component
public class SpringUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext = null;

    /**
     * 根据Bean唯一名获取Bean实例
     */
    public static <T> T getBean(String name) {
        return (T) applicationContext.getBean(name);
    }

    /**
     * 根据接口Class获取所有实现该接口的Bean实例
     */
    public static <T> Map<String, T> getBeansOfType(Class<T> clzz) {
        return applicationContext.getBeansOfType(clzz);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}