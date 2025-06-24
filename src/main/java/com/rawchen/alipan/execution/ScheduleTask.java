package com.rawchen.alipan.execution;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rawchen.alipan.config.Constants;
import com.rawchen.alipan.controller.ApiController;
import com.rawchen.alipan.entity.TokenBody;
import com.rawchen.alipan.utils.FileUtil;
import com.rawchen.alipan.utils.HttpClientUtil;
import com.rawchen.alipan.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
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
@Slf4j
public class ScheduleTask {

	@Value("${alipan.api_url}")
	String apiUrl;

	@Value("${alipan.open_api_url}")
	String openApiUrl;

	@Value("${alipan.oauth_token_url}")
	String oauthTokenUrl;

	@Autowired
	ApiController apiController;

	/**
	 * 启动执行一次 && 每2小时执行一次刷新access_token
	 */
	@Scheduled(fixedRate = 7200 * 1000)
	private void scheduleTask() {

		log.info("刷新access_token: {}", DateUtil.date());
		JSONObject paramJson = new JSONObject();
		paramJson.put("grant_type", "refresh_token");
		paramJson.put("refresh_token", Constants.getRefreshToken());
		String result = HttpClientUtil.doPost(apiUrl + "/account/token", paramJson.toString());
		JSONObject jsonObject = JSONObject.parseObject(result);
		if (jsonObject.get("code") != null) {
			log.error("scheduleTask()刷新access_token出错：{}", result);
		} else {
			// 刷新一次refresh_token到original-token.txt
			String refreshToken = (String) jsonObject.get("refresh_token");
			FileUtil.stringToTextFile(refreshToken, new File(System.getProperty("user.dir") + File.separator + "original-token.txt"));
			Constants.setRefreshToken(refreshToken);
			Constants.setAccessToken((String) jsonObject.get("access_token"));
			Constants.setUserId((String) jsonObject.get("user_id"));
			Constants.setDeviceId((String) jsonObject.get("device_id"));
		}

		log.info("刷新access_token_open: {}", DateUtil.date());
		TokenBody tokenBodyOpen = new TokenBody();
		tokenBodyOpen.setGrantType("refresh_token");
		tokenBodyOpen.setRefreshToken(Constants.getRefreshTokenOpen());
		String resultOpen = HttpClientUtil.doPost(oauthTokenUrl, JSON.toJSONString(tokenBodyOpen));
		JSONObject jsonObjectOpen = JSONObject.parseObject(resultOpen);
		if (jsonObjectOpen.get("code") != null) {
			log.error("scheduleTask()刷新access_token_open出错：{}", StrUtil.cleanBlank(resultOpen) );
		} else {
			// 刷新一次refresh_token到open-token.txt
			String refreshTokenOpen = jsonObjectOpen.getString("refresh_token");
			FileUtil.stringToTextFile(refreshTokenOpen, new File(System.getProperty("user.dir") + File.separator + "open-token.txt"));
			Constants.setRefreshTokenOpen(refreshTokenOpen);
			Constants.setAccessTokenOpen(jsonObjectOpen.getString("access_token"));

			log.info("设置default_drive_id: {}", DateUtil.date());
			Map<String, String> headers = new HashMap<>();
			headers.put("Authorization", "Bearer " + Constants.ACCESS_TOKEN_OPEN);
			String driveResult = HttpClientUtil.doPost(openApiUrl + "/adrive/v1.0/user/getDriveInfo", null, headers);
			JSONObject jsonDriveObject = JSONObject.parseObject(driveResult);
			Constants.setDefaultDriveId(jsonDriveObject.getString("default_drive_id"));
		}
	}
}
