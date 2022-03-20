package com.rawchen.alipan.utils;

/**
 * @author RawChen
 * @date 2022-03-20 11:08
 */
public class StringUtil {

	/**
	 * 给密码去空格，去多于换行，去制表符
	 *
	 * @return
	 */
	public static String clearStr(String str) {
		if (str != null) {
			str = str.replaceAll("\\s*", "");
			return str;
		}
		return null;
	}
}
