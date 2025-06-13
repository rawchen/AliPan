package com.rawchen.alipan.execution;

import com.rawchen.alipan.config.Constants;
import com.rawchen.alipan.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class InitialOperation implements CommandLineRunner {

	@Override
	public void run(String... args) throws Exception {
		//如果jar同级目录不存在该配置文件则重新创建
		String absAddress = System.getProperty("user.dir");
		log.info("JAR同级目录：" + absAddress);
		File file = new File(absAddress + File.separator + "original-token.txt");
		if (!file.exists()) {
			file.createNewFile();
		}
		//拿到配置文件里的refresh_token放到全局Constants
		String s = FileUtil.textFileToString(file);
		Constants.setRefreshToken(s);

		File fileOpen = new File(absAddress + File.separator + "open-token.txt");
		if (!fileOpen.exists()) {
			fileOpen.createNewFile();
		}
		String sOpen = FileUtil.textFileToString(fileOpen);
		Constants.setRefreshTokenOpen(sOpen);
	}
}
