package com.rawchen.alipan.execution;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.rawchen.alipan.config.Constants;
import com.rawchen.alipan.utils.HttpClientUtil;
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
	}
}
