package com.rawchen.alipan.config;


import org.springframework.stereotype.Component;

@Component
public class Const {


//	public static final String REFRESH_TOKEN = PropertiesUtil.getProperty("refresh_token");
//	public static final String API_URL = PropertiesUtil.getProperty("api_url");


	// 设置连接主机服务超时main间
	public static final int CONNECT_TIMEOUT = 35000;
	// 设置连接请求超时时间
	public static final int CONNECTION_REQUEST_TIMEOUT = 35000;
	// 设置读取数据连接超时时间
	public static final int SOCKET_TIMEOUT = 60000;


}
