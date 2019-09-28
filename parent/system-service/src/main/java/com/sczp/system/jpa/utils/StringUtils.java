package com.sczp.system.jpa.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 
* Title: StringUtils 
* Description:  字符串操作工具类
* @author dicky  
* @date 2018年7月5日 上午10:16:11
 */
public class StringUtils {
	
	public static final String[] EMPTY_STRING_ARRAY = new String[0];
	private StringUtils() {}

	/**
	 * 判断是否空白
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isBlank(final String str) {
		int strLen;
		if ((str == null) || ((strLen = str.length()) == 0)) {
            return true;
        }
		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public static String[] split(String content, String separatorChars) {
		return splitWorker(content, separatorChars, -1, false);
	}

	private static String[] splitWorker(String str, String separatorChars, int max, boolean preserveAllTokens) {
		if (str == null) {
			return EMPTY_STRING_ARRAY;
		}
		int len = str.length();
		if (len == 0) {
			return EMPTY_STRING_ARRAY;
		}
		List<String> list = new ArrayList<>();
		int sizePlus1 = 1;
		int i = 0;
		int start = 0;
		boolean match = false;
		boolean lastMatch = false;
		if (separatorChars == null) {
			while (i < len) {
				if (Character.isWhitespace(str.charAt(i))) {
					if (match || preserveAllTokens) {
						lastMatch = true;
						if (sizePlus1++ == max) {
							i = len;
							lastMatch = false;
						}
						list.add(str.substring(start, i));
						match = false;
					}
					start = ++i;
					continue;
				}
				lastMatch = false;
				match = true;
				i++;
			}
		} else if (separatorChars.length() == 1) {
			char sep = separatorChars.charAt(0);
			while (i < len) {
				if (str.charAt(i) == sep) {
					if (match || preserveAllTokens) {
						lastMatch = true;
						if (sizePlus1++ == max) {
							i = len;
							lastMatch = false;
						}
						list.add(str.substring(start, i));
						match = false;
					}
					start = ++i;
					continue;
				}
				lastMatch = false;
				match = true;
				i++;
			}
		} else {
			while (i < len) {
				if (separatorChars.indexOf(str.charAt(i)) >= 0) {
					if (match || preserveAllTokens) {
						lastMatch = true;
						if (sizePlus1++ == max) {
							i = len;
							lastMatch = false;
						}
						list.add(str.substring(start, i));
						match = false;
					}
					start = ++i;
					continue;
				}
				lastMatch = false;
				match = true;
				i++;
			}
		}
		if (match || (preserveAllTokens && lastMatch)) {
			list.add(str.substring(start, i));
		}
		return  list.toArray(EMPTY_STRING_ARRAY);
	}
	
	
	/***
	 * 下划线命名转为驼峰命名
	 * @param string
	 * @return
	 */
	public static String underlineToHump(String string){
		StringBuilder result=new StringBuilder();
		String[] array= string.split("_");
		for(String str:array){
			if(result.length()==0){
				result.append(str.toLowerCase());
			}else{
				result.append(str.substring(0, 1).toUpperCase());
				result.append(str.substring(1).toLowerCase());
			}
		}
		return result.toString();
	}

	
	/**
	 * 驼峰命名转为下划线命名    sysUserId ==> sys_user_id
	 * @param string
	 * @return
	 */
	 public static String humpToUnderline(String string){
		 StringBuilder sbuilder=new StringBuilder(string);
	        int temp=0;//定位
	        for(int i=0;i<string.length();i++){
	            if(Character.isUpperCase(string.charAt(i))){
	            	sbuilder.insert(i+temp, "_");
	                temp+=1;
	            }
	        }
		return sbuilder.toString().toLowerCase();
	 }
}
