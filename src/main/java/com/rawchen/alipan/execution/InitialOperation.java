package com.rawchen.alipan.execution;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

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

	}
}
