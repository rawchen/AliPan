package com.rawchen.alipan.execution;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.rawchen.alipan.config.Constants;
import com.rawchen.alipan.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

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

	/**
	 * 启动执行一次 && 每7200秒执行一次刷新access_token
	 */
	@Scheduled(fixedRate = 7200 * 1000)
	private void scheduleTask() {
		System.err.println("2h定时任务执行: " + DateUtil.date());
		JSONObject paramJson = new JSONObject();
		paramJson.put("grant_type", "refresh_token");
		paramJson.put("refresh_token", Constants.getRefreshToken());
		String result = HttpClientUtil.doPost(apiUrl + "/account/token", paramJson.toString());
		JSONObject jsonObject = JSONObject.parseObject(result);
		Constants.setAccessToken((String) jsonObject.get("access_token"));
		Constants.setDefaultDriveId((String) jsonObject.get("default_drive_id"));
//		System.out.println(jsonObject.get("access_token"));
	}

	/**
	 * 调用目录接口，第一次延迟1分钟后执行，之后按每1小时调用一次
	 */
	@Scheduled(initialDelay = 60 * 1000, fixedRate = 3600 * 1000)
	private void scheduleTaskToGetFolder() {
		JSONObject requestJson = new JSONObject();
		requestJson.put("param1", 30);
		requestJson.put("all", false);
		requestJson.put("drive_id", Constants.DEFAULT_DRIVE_ID);
		requestJson.put("fields", "*");
		requestJson.put("image_thumbnail_process", "image/resize,w_50");
		requestJson.put("image_url_process", "image/resize,w_1920/format,jpeg");
		requestJson.put("limit", 100);
		requestJson.put("url_expire_sec", 14400);
		requestJson.put("order_by", "name");
		requestJson.put("order_direction", "ASC");
		requestJson.put("parent_file_id", "root");
		requestJson.put("video_thumbnail_process", "video/snapshot,t_0,f_jpg,w_50");

		Map<String, String> headerMap = new HashMap<>();
		headerMap.put("Content-Type", "application/json");
		headerMap.put("Authorization", "Bearer " + Constants.ACCESS_TOKEN);
		HttpClientUtil.doPost(apiUrl + "/file/list", requestJson.toString(), headerMap);
	}
}
