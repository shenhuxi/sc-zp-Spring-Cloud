package com.sczp.common.util.synchronized_util;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 线程安全的转换类
 */
public class SimpleDateFormatUtil {
    private static ThreadLocal<SimpleDateFormat> threadLocal;
    static {
        threadLocal = new ThreadLocal() {
            @Override
            protected SimpleDateFormat initialValue() {
                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            }
        };
    }

    public static Date parse(String dateStr) throws ParseException {
        return threadLocal.get().parse(dateStr);
    }

    public static String format(Date date) {
        return threadLocal.get().format(date);
    }

    @Test
    public void  testDateFormat(){
        String format = SimpleDateFormatUtil.format(new Date());
        System.out.println(format);
    }
}
