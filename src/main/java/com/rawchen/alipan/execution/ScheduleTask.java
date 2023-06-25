package com.rawchen.alipan.execution;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.rawchen.alipan.config.Constants;
import com.rawchen.alipan.controller.ApiController;
import com.rawchen.alipan.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Spring Boot定时任务
 *
 * @author RawChen
 * @since 2021-10-28 20:15
 */
@Component
@EnableScheduling
public class ScheduleTask {

	@Value("${alipan.api_url}")
	String apiUrl;

	@Value("${alipan.oauth_token_url}")
	String oauthTokenUrl;

	@Autowired
	ApiController apiController;

	/**
	 * 启动执行一次 && 每2小时执行一次刷新access_token
	 */
	@Scheduled(fixedRate = 7200 * 1000)
	private void scheduleTask() {
		System.out.println("刷新access_token: " + DateUtil.date());
		JSONObject paramJson = new JSONObject();
		paramJson.put("grant_type", "refresh_token");
		paramJson.put("refresh_token", Constants.getRefreshToken());
		String result = HttpClientUtil.doPost(apiUrl + "/account/token", paramJson.toString());
		JSONObject jsonObject = JSONObject.parseObject(result);
		Constants.setAccessToken((String) jsonObject.get("access_token"));
		Constants.setDefaultDriveId((String) jsonObject.get("default_drive_id"));
		Constants.setUserId((String) jsonObject.get("user_id"));
		Constants.setDeviceId((String) jsonObject.get("device_id"));
//		System.out.println(jsonObject.get("access_token"));

		System.out.println("刷新access_token_open: " + DateUtil.date());
		JSONObject paramJsonOpen = new JSONObject();
		paramJsonOpen.put("client_id", "");
		paramJsonOpen.put("client_secret", "");
		paramJsonOpen.put("grant_type", "refresh_token");
		paramJsonOpen.put("refresh_token", Constants.getRefreshTokenOpen());
		String resultOpen = HttpClientUtil.doPost(oauthTokenUrl, paramJsonOpen.toString());
		if (!resultOpen.toLowerCase().contains("502 bad gateway")) {
			JSONObject jsonObjectOpen = JSONObject.parseObject(resultOpen);
			Constants.setAccessToken(jsonObjectOpen.getString("access_token"));
			Constants.setDefaultDriveId(jsonObjectOpen.getString("default_drive_id"));
		} else {
			// 将导致token过期
			System.out.println("定时获取access_token_open: 502 bad gateway. " + DateUtil.date());
		}
	}

	/**
	 * 调用目录接口，第一次延迟1分钟后执行，之后按每1小时调用一次
	 */
	@Scheduled(initialDelay = 60 * 1000, fixedRate = 3600 * 1000)
	private void scheduleTaskToGetFolder() {
		apiController.getFolder("root", null);
//		JSONObject requestJson = new JSONObject();
//		requestJson.put("param1", 30);
//		requestJson.put("all", false);
//		requestJson.put("drive_id", Constants.DEFAULT_DRIVE_ID);
//		requestJson.put("fields", "*");
//		requestJson.put("image_thumbnail_process", "image/resize,w_50");
//		requestJson.put("image_url_process", "image/resize,w_1920/format,jpeg");
//		requestJson.put("limit", 100);
//		requestJson.put("url_expire_sec", 14400);
//		requestJson.put("order_by", "name");
//		requestJson.put("order_direction", "ASC");
//		requestJson.put("parent_file_id", "root");
//		requestJson.put("video_thumbnail_process", "video/snapshot,t_0,f_jpg,w_50");
//
//		Map<String, String> headerMap = new HashMap<>();
//		headerMap.put("Content-Type", "application/json");
//		headerMap.put("Authorization", "Bearer " + Constants.ACCESS_TOKEN);
//		HttpClientUtil.doPost(apiUrl + "/file/list", requestJson.toString(), headerMap);
	}
}
