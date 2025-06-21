package com.rawchen.alipan.utils;

import cn.hutool.core.util.StrUtil;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

	/**
	 * SHA256加密
	 *
	 * @param str
	 * @return
	 */
	public static String getSHA256(String str) {
		MessageDigest messageDigest;
		String encodestr = "";
		try {
			messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.update(str.getBytes("UTF-8"));
			encodestr = byte2Hex(messageDigest.digest());
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return encodestr;
	}

	/**
	 * 将byte转为16进制
	 *
	 * @param bytes
	 * @return
	 */
	public static String byte2Hex(byte[] bytes) {
		StringBuffer stringBuffer = new StringBuffer();
		String temp = null;
		for (int i = 0; i < bytes.length; i++) {
			temp = Integer.toHexString(bytes[i] & 0xFF);
			if (temp.length() == 1) {
				//1得到一位的进行补0操作
				stringBuffer.append("0");
			}
			stringBuffer.append(temp);
		}
		return stringBuffer.toString();
	}

	/**
	 * 是否为空
	 *
	 * @param s
	 * @return
	 */
	public static boolean isEmpty(String s) {
		return s == null || "".equals(s);
	}

	public static String subStringShort(String refreshToken) {
		if (StrUtil.isEmpty(refreshToken)) {
			return null;
		} else if (refreshToken.length() <= 30) {
			// 如果字符串长度小于等于30，直接返回原字符串
			return refreshToken;
		} else {
			// 如果字符串长度大于30，截取前30个字符并添加省略号
			return refreshToken.substring(0, 30) + "...";
		}
	}
}
