package com.rawchen.alipan.config;
import org.springframework.stereotype.Component;

/**
 * 常量类，不用动
 */
@Component
public class Constants {

	// 访问令牌
	public static String ACCESS_TOKEN = "";
	// 刷新令牌
	public static String REFRESH_TOKEN = "";
	// 默认驱动器ID
	public static String DEFAULT_DRIVE_ID = "";
	// 签名私钥
	public static String PRIVATE_KEY = "";
	// 签名公钥
	public static String PUBLIC_KEY = "";
	// 应用ID
	public static final String APP_ID = "5dde4e1bdf9e4966b387ba58f4b3fdc3";
	// 用户ID
	public static String USER_ID = "";
	// 设备ID
	public static String DEVICE_ID = "";

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

	public static String getRefreshToken() {
		return REFRESH_TOKEN;
	}

	public static void setRefreshToken(String refreshToken) {
		REFRESH_TOKEN = refreshToken;
	}

	public static String getDefaultDriveId() {
		return DEFAULT_DRIVE_ID;
	}

	public static void setDefaultDriveId(String defaultDriveId) {
		DEFAULT_DRIVE_ID = defaultDriveId;
	}

	public static String getUserId() {
		return USER_ID;
	}

	public static void setUserId(String userId) {
		Constants.USER_ID = userId;
	}

	public static String getDeviceId() {
		return DEVICE_ID;
	}

	public static void setDeviceId(String deviceId) {
		DEVICE_ID = deviceId;
	}

	public static void setPrivateKey(String privateKey) {
		Constants.PRIVATE_KEY = privateKey;
	}

	public static String getPrivateKey() {
		return PRIVATE_KEY;
	}

	public static String getPublicKey() {
		return PUBLIC_KEY;
	}

	public static void setPublicKey(String publicKey) {
		PUBLIC_KEY = publicKey;
	}
}
