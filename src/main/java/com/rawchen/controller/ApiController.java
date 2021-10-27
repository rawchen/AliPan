package com.rawchen.controller;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rawchen.alipan.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ApiController {

	@Value("${refresh_token}")
	String refresh_token;

	@Value("${api_url}")
	String api_url;

	@RequestMapping("/path")
	@ResponseBody
	public Map<String,Object> selectByPrimaryKey(Model model) {
		// 获取访问令牌
		JSONObject paramJson = new JSONObject();
		paramJson.put("refresh_token", refresh_token);
		paramJson.put("grant_type", "refresh_token");
		String result = HttpClientUtil.doPost(api_url + "/account/token", paramJson.toString());
		System.out.println(result);

		//后续业务接口请求
		JSONObject requestJson = new JSONObject();
		requestJson.put("param1", 30);
		requestJson.put("all", false);
		requestJson.put("drive_id", "1030023");
		requestJson.put("fields", "*");
		requestJson.put("image_thumbnail_process", "image/resize,w_50");
		requestJson.put("image_url_process", "image/resize,w_1920/format,jpeg");
		requestJson.put("limit", 100);
		requestJson.put("order_by", "updated_at");
		requestJson.put("order_direction", "DESC");
		requestJson.put("parent_file_id", "60d00f6c1bf52cd0c0184a47a806edb2632bb59f");
		requestJson.put("video_thumbnail_process", "video/snapshot,t_0,f_jpg,w_50");

		Map headerMap = new HashMap();
		headerMap.put("Content-Type", "application/json");
		headerMap.put("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI3OGQwMzA0M2ZlMmY0ZGRjYjgzMGNkYmU4NjEzZTJlZSIsImN1c3RvbUpzb24iOiJ7XCJjbGllbnRJZFwiOlwiMjVkelgzdmJZcWt0Vnh5WFwiLFwiZG9tYWluSWRcIjpcImJqMjlcIixcInNjb3BlXCI6W1wiRFJJVkUuQUxMXCIsXCJTSEFSRS5BTExcIixcIkZJTEUuQUxMXCIsXCJVU0VSLkFMTFwiLFwiU1RPUkFHRS5BTExcIixcIlNUT1JBR0VGSUxFLkxJU1RcIixcIkJBVENIXCIsXCJPQVVUSC5BTExcIixcIklNQUdFLkFMTFwiLFwiSU5WSVRFLkFMTFwiLFwiQUNDT1VOVC5BTExcIl0sXCJyb2xlXCI6XCJ1c2VyXCIsXCJyZWZcIjpcImh0dHBzOi8vd3d3LmFsaXl1bmRyaXZlLmNvbS9cIn0iLCJleHAiOjE2MzUyNjU3NDcsImlhdCI6MTYzNTI1ODQ4N30.NUR5nrlMXMZS95g0JIaRMUnBG2K4atfkTL92-XPIu-qLpl0Sug_YSelSLfsCdkEg80aMuT_EhyI7o8_kjQjuxn8OkDQ8AnOucsLDJKu0tWjFhhrPYP3_F7SKptWB4v7ppRNTPfOtqEtoPuvQAHmdyPfw1Rzth58N7p7z8m9pwDY");

		String result2 = HttpClientUtil.requestPayload(api_url + "/file/list",
				requestJson.toString(), headerMap);
		System.out.println(result2);
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> mapMeta = new HashMap<>();
		mapMeta.put("code",200);
		mapMeta.put("msg","success");

		Map map_3 = new HashMap();
		try {
			map_3 = new ObjectMapper().readValue(result2, Map.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		map.put("data",map_3);
		map.put("meta",mapMeta);


		// MP3详细
		System.out.println(map_3.get("items"));
		Object items = map_3.get("items");


		return map;
	}

	@GetMapping("/info")
	@ResponseBody
	public Map<String,Object> info(Model model) {
		Map<String, Object> map = new HashMap<>();

		Map<String, Object> mapData = new HashMap<>();
		Map<String, Object> mapMeta = new HashMap<>();

		mapData.put("title","123");
		mapData.put("script","");
		mapData.put("roots",new String[]{"home"});
		mapData.put("logo","");
		mapData.put("footer_text","rawchen.com");
		mapData.put("footer_url","rawchen.com");
		mapData.put("music_img","https://cdn.cooluc.com/m.png");
		mapData.put("check_update",true);
		mapData.put("autoplay",true);

		Map<String, Object> previewMap = new HashMap<>();
		previewMap.put("url","");
		previewMap.put("pre_process",new String[0]);
		previewMap.put("extensions",new String[0]);
		previewMap.put("text",new String[]{"txt", "htm", "html", "xml", "java", "properties", "sql", "js", "md", "json", "conf", "ini", "vue", "php", "py", "bat", "gitignore", "yml", "go", "sh", "c", "cpp", "h", "hpp"});
		previewMap.put("max_size",0);

		mapData.put("preview",previewMap);

		mapMeta.put("code",200);
		mapMeta.put("msg","success");

		map.put("data",mapData);
		map.put("meta",mapMeta);
		return map;
	}
}