package com.rawchen.alipan.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rawchen.alipan.config.Constants;
import com.rawchen.alipan.entity.PanFile;
import com.rawchen.alipan.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	public Map<String, Object> getFile(@PathVariable("fileId") String fileId) {

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
	public Map<String, Object> getFolder(@PathVariable("fileId") String fileId) {

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

	@GetMapping(path = "/t")
	public void r1(HttpServletRequest request, HttpServletResponse response) {
		try {
			response.sendRedirect("https://bj29.cn-beijing.data.alicloudccp.com/LQNLJSNv%2F1030023%2F60b107e2fcc924f1282d49a38cc5a489398685ea%2F60b107e25893e4688d7d4475805c574b23cda10c?di=bj29&dr=1030023&f=60b107e2fcc924f1282d49a38cc5a489398685ea&response-content-disposition=attachment%3B%20filename%2A%3DUTF-8%27%27cover.jpg&u=78d03043fe2f4ddcb830cdbe8613e2ee&x-oss-access-key-id=LTAIsE5mAn2F493Q&x-oss-additional-headers=referer&x-oss-expires=1635523246&x-oss-signature=%2B7Imb4DAiOkmY38uO0iJMgx1k8DVUzkYCFFbsIHaGNM%3D&x-oss-signature-version=OSS2");
		} catch (IOException e) {
			e.printStackTrace();
		}
//		return "redirect:"+"https://bj29.cn-beijing.data.alicloudccp.com/LQNLJSNv%2F1030023%2F60b107e2fcc924f1282d49a38cc5a489398685ea%2F60b107e25893e4688d7d4475805c574b23cda10c?di=bj29&dr=1030023&f=60b107e2fcc924f1282d49a38cc5a489398685ea&response-content-disposition=attachment%3B%20filename%2A%3DUTF-8%27%27cover.jpg&u=78d03043fe2f4ddcb830cdbe8613e2ee&x-oss-access-key-id=LTAIsE5mAn2F493Q&x-oss-additional-headers=referer&x-oss-expires=1635523246&x-oss-signature=%2B7Imb4DAiOkmY38uO0iJMgx1k8DVUzkYCFFbsIHaGNM%3D&x-oss-signature-version=OSS2";
	}

	/**
	 * 文件夹列表，返回的json带items
	 *
	 * @param fileId
	 * @return
	 */
	@RequestMapping(value = "/folder2/{fileId}")
	public String getFolder2(@PathVariable("fileId") String fileId, Model model) {

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
		JSONArray jsonArray = jsonObject.getJSONArray("items");

		List<PanFile> panFileList = new ArrayList<>();
		//获取每个文件json对象
		for (Object o : jsonArray) {
			JSONObject dataObj = JSONObject.parseObject(o.toString());
			if ("folder".equals(dataObj.get("type"))) {
				PanFile panFile = new PanFile();
				panFile.setType("folder");
				panFile.setName((String) dataObj.get("name"));
				panFile.setUrl((String) dataObj.get("file_id"));
				panFileList.add(panFile);
			} else {
				PanFile panFile = new PanFile();
				panFile.setType("file");
				panFile.setName((String) dataObj.get("name"));
				panFile.setUrl((String) dataObj.get("file_id"));
				panFileList.add(panFile);
			}

		}
		model.addAttribute("data", panFileList);
		return "index";
	}

	/**
	 * 手动刷新token
	 *
	 * @return
	 */
	@ResponseBody
	@GetMapping(value = "/refresh")
	public Map<String, JSONObject> test() {

		JSONObject requestJson = new JSONObject();
		requestJson.put("refresh_token", refreshToken);
		Map<String, String> headerMap = new HashMap<>();
		headerMap.put("Content-Type", "application/json");
		String result = HttpClientUtil.doPost(apiUrl + "/token/refresh", requestJson.toString(), headerMap);
		JSONObject jsonObject = JSONObject.parseObject(result);
		Constants.setAccessToken((String) jsonObject.get("access_token"));
		Constants.setDefaultDriveId((String) jsonObject.get("default_drive_id"));
		Map<String, JSONObject> map = new HashMap<>();
		map.put("data", jsonObject);
		return map;
	}
}