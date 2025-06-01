package com.rawchen.alipan.execution;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rawchen.alipan.config.Constants;
import com.rawchen.alipan.controller.ApiController;
import com.rawchen.alipan.entity.TokenBody;
import com.rawchen.alipan.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
		log.info("刷新access_token: " + DateUtil.date());
		JSONObject paramJson = new JSONObject();
		paramJson.put("grant_type", "refresh_token");
		paramJson.put("refresh_token", Constants.getRefreshToken());
		String result = HttpClientUtil.doPost(apiUrl + "/account/token", paramJson.toString());
		JSONObject jsonObject = JSONObject.parseObject(result);
		Constants.setAccessToken((String) jsonObject.get("access_token"));
		Constants.setDefaultDriveId((String) jsonObject.get("default_drive_id"));
		Constants.setUserId((String) jsonObject.get("user_id"));
		Constants.setDeviceId((String) jsonObject.get("device_id"));
		log.info("刷新access_token_open: " + DateUtil.date());
		TokenBody tokenBodyOpen = new TokenBody();
		tokenBodyOpen.setGrantType("refresh_token");
		tokenBodyOpen.setRefreshToken(Constants.getRefreshTokenOpen());
		String resultOpen = HttpClientUtil.doPost(oauthTokenUrl, JSON.toJSONString(tokenBodyOpen));
		if (!resultOpen.toLowerCase().contains("502 bad gateway")) {
			JSONObject jsonObjectOpen = JSONObject.parseObject(resultOpen);
			if ("Too Many Requests".equals(jsonObjectOpen.getString("code"))) {
				log.error("定时获取access_token_open: Too Many Requests. " + DateUtil.date());
			} else if (!StrUtil.isEmpty(jsonObjectOpen.getString("access_token"))) {
				Constants.setAccessTokenOpen(jsonObjectOpen.getString("access_token"));
				Constants.setDefaultDriveId(jsonObjectOpen.getString("default_drive_id"));
			} else {
				log.info("定时获取access_token_open: " + resultOpen);
			}
		} else {
			// 将导致token过期
			log.error("定时获取access_token_open: 502 bad gateway. ");
		}
	}

	/**
	 * 调用目录接口，第一次延迟1分钟后执行，之后按每1小时调用一次
	 */
	@Scheduled(initialDelay = 60 * 1000, fixedRate = 3600 * 1000)
	private void scheduleTaskToGetFolder() {
		apiController.getFolder("root", null);
	}
}
