package com.rawchen.alipan.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * @author RawChen
 * @date 2021-12-17 9:34
 */
public class FileUtil {
	/**
	 * 读取文件的内容第一行
	 *
	 * @param file 想要读取的文件对象
	 * @return 返回文件内容
	 */
	public static String textFileToString(File file) {
		String result = "";
		try {
			//构造一个BufferedReader类来读取文件
			BufferedReader br = new BufferedReader(new FileReader(file));
			String s = null;
			//使用readLine方法，一次读一行
			if ((s = br.readLine()) != null) {
				result = s;
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
