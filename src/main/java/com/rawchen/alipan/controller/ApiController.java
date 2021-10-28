package com.rawchen.alipan.controller;

import com.alibaba.fastjson.JSONObject;
import com.rawchen.alipan.config.Constants;
import com.rawchen.alipan.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@Controller
public class ApiController {

	@Value("${alipan.refresh_token}")
	String refreshToken;


	@Value("${alipan.parent_file_id}")
	String parentFileId;

	@Value("${alipan.api_url}")
	String apiUrl;

	@Value("${alipan.referer_url}")
	String refererURL;

	/**
	 * 获取文件
	 *
	 * @param fileId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/file/{fileId}")
	public Map<String,Object> getFile(@PathVariable("fileId") String fileId) {

		JSONObject requestJson = new JSONObject();
		requestJson.put("drive_id", Constants.DEFAULT_DRIVE_ID);
		requestJson.put("file_id", fileId);
		Map<String, String> headerMap = new HashMap<>();
		headerMap.put("Content-Type", "application/json");
		headerMap.put("Referer", refererURL);
		headerMap.put("Authorization", "Bearer " + Constants.ACCESS_TOKEN);

		String result = HttpClientUtil.doPost(apiUrl + "/file/get",
				requestJson.toString(), headerMap);
		JSONObject jsonObject = JSONObject.parseObject(result);



		Map<String, Object> map = new HashMap<>();
		map.put("data", jsonObject);
		return map;
	}

	/**
	 * 文件夹列表，返回的json带items
	 *
	 * @param fileId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/folder/{fileId}")
	public Map<String,Object> getFolder(@PathVariable("fileId") String fileId) {

		JSONObject requestJson = new JSONObject();
		requestJson.put("param1", 30);
		requestJson.put("all", false);
		requestJson.put("drive_id", Constants.DEFAULT_DRIVE_ID);
		requestJson.put("fields", "*");
		requestJson.put("image_thumbnail_process", "image/resize,w_50");
		requestJson.put("image_url_process", "image/resize,w_1920/format,jpeg");
		requestJson.put("limit", 100);
		requestJson.put("order_by", "updated_at");
		requestJson.put("order_direction", "DESC");
		requestJson.put("parent_file_id", fileId);
		requestJson.put("video_thumbnail_process", "video/snapshot,t_0,f_jpg,w_50");

		Map headerMap = new HashMap();
		headerMap.put("Content-Type", "application/json");
		headerMap.put("Authorization", "Bearer " + Constants.ACCESS_TOKEN);

		String result = HttpClientUtil.doPost(apiUrl + "/file/list",
				requestJson.toString(), headerMap);
		JSONObject jsonObject = JSONObject.parseObject(result);
		Map<String, Object> map = new HashMap<>();
		map.put("data", jsonObject);
		return map;
	}

	/**
	 * 手动刷新token
	 *
	 * @return
	 */
	@ResponseBody
	@GetMapping(value = "/refresh")
	public Map<String,JSONObject> test() {

		JSONObject requestJson = new JSONObject();
		requestJson.put("refresh_token", refreshToken);
		Map<String, String> headerMap = new HashMap<>();
		headerMap.put("Content-Type", "application/json");
		String result = HttpClientUtil.doPost(apiUrl + "/token/refresh", requestJson.toString(), headerMap);
		JSONObject jsonObject = JSONObject.parseObject(result);
		Constants.setAccessToken((String) jsonObject.get("access_token"));
		Constants.setDefaultDriveId((String) jsonObject.get("default_drive_id"));
		Map<String, JSONObject> map = new HashMap<>();
		map.put("data",jsonObject);
		return map;
	}
}