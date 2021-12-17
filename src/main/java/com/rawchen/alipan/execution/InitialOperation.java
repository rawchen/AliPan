package com.rawchen.alipan.execution;

import com.rawchen.alipan.config.Constants;
import com.rawchen.alipan.utils.FileUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Spring Boot启动后自动执行
 *
 * @author RawChen
 * @since 2021-10-28 19:59
 */
@Component
@Order(1)
public class InitialOperation implements CommandLineRunner {

	@Override
	public void run(String... args) throws Exception {
		//如果jar同级目录不存在该token文件则生成文件，并要求用户填入
		String absAddress = System.getProperty("user.dir");
		System.out.println(absAddress);
		File file = new File(absAddress + File.separator + "AliPanConfig");
		if (!file.exists()) {
			file.createNewFile();
		}

		String s = FileUtil.textFileToString(file);
		Constants.setRefreshToken(s);
	}
}
