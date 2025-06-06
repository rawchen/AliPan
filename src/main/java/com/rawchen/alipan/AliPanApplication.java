package com.rawchen.alipan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class AliPanApplication {

	public static void main(String[] args) {
		SpringApplication.run(AliPanApplication.class, args);
	}

}
