package com.rawchen.alipan.config;
import org.springframework.stereotype.Component;

/**
 * 常量类，不用动
 */
@Component
public class Constants {

	// 访问令牌
	public static String ACCESS_TOKEN = "";
	// 默认驱动器ID
	public static String DEFAULT_DRIVE_ID = "";

	// 设置连接主机服务超时时间
	public static final int CONNECT_TIMEOUT = 35000;
	// 设置连接请求超时时间
	public static final int CONNECTION_REQUEST_TIMEOUT = 35000;
	// 设置读取数据连接超时时间
	public static final int SOCKET_TIMEOUT = 60000;


	public static String getAccessToken() {
		return ACCESS_TOKEN;
	}

	public static void setAccessToken(String accessToken) {
		ACCESS_TOKEN = accessToken;
	}

	public static String getDefaultDriveId() {
		return DEFAULT_DRIVE_ID;
	}

	public static void setDefaultDriveId(String defaultDriveId) {
		DEFAULT_DRIVE_ID = defaultDriveId;
	}
}
