package com.rawchen.alipan.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rawchen.alipan.config.Constants;
import com.rawchen.alipan.entity.PanFile;
import com.rawchen.alipan.utils.FileUtil;
import com.rawchen.alipan.utils.HttpClientUtil;
import com.rawchen.alipan.utils.SignUtil;
import com.rawchen.alipan.utils.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@Controller
public class ApiController {

	@Value("${alipan.parent_file_id}")
	String parentFileId;

	@Value("${alipan.api_url}")
	String apiUrl;

	@Value("${alipan.api_url_v3}")
	String apiUrlV3;

	@Value("${alipan.oauth_token_url}")
	String oauthTokenUrl;

	@Value("${alipan.open_api_url}")
	String openApiUrl;

	@Value("${alipan.referer_url}")
	String refererURL;

	@Value("${alipan.password_file_name}")
	String passwordFileName;

	@Value("${alipan.app_id}")
	String appId;

	/**
	 * 文件对象
	 *
	 * @param fileId
	 * @return
	 */
	@ResponseBody
	@PostMapping(value = "/getFile/{fileId}")
	public PanFile getFile(@PathVariable("fileId") String fileId) {

		List<String> sign = SignUtil.sign(appId, Constants.DEVICE_ID, Constants.USER_ID, "0");
		createSession(sign.get(0), sign.get(2));

		JSONObject requestJson = new JSONObject();
		requestJson.put("drive_id", Constants.DEFAULT_DRIVE_ID);
		requestJson.put("file_id", fileId);
		Map<String, String> headerMap = new HashMap<>();
		headerMap.put("Content-Type", "application/json");
		headerMap.put("Referer", refererURL);
		headerMap.put("Authorization", "Bearer " + Constants.ACCESS_TOKEN);
		headerMap.put("x-canary", "client=web,app=adrive,version=v3.17.0");
		headerMap.put("x-device-id", Constants.DEVICE_ID);
		headerMap.put("x-signature", sign.get(2));

		String result = HttpClientUtil.doPost(apiUrl + "/file/get",
				requestJson.toString(), headerMap);
		JSONObject jsonObject = JSONObject.parseObject(result);
		PanFile file = new PanFile();
		file.setFileId((String) jsonObject.get("file_id"));
		file.setType((String) jsonObject.get("type"));
		file.setName((String) jsonObject.get("name"));
		file.setCreatedAt((String) jsonObject.get("created_at"));
		file.setFileExtension((String) jsonObject.get("file_extension"));
		file.setParentFileId((String) jsonObject.get("parent_file_id"));
		file.setSize(((Number) jsonObject.get("size")).longValue());
		file.setUrl((String) jsonObject.get("url"));
		return file;
	}

	/**
	 * 文件列表
	 *
	 * @param fileId
	 * @return
	 */
	@ResponseBody
	@PostMapping(value = "/getFolder/{fileId}")
	public List<PanFile> getFolder(@PathVariable("fileId") String fileId, @RequestParam(required = false) String password) {
		JSONObject requestJson = new JSONObject();
		requestJson.put("param1", 30);
		requestJson.put("all", false);
		requestJson.put("drive_id", Constants.DEFAULT_DRIVE_ID);
		requestJson.put("fields", "*");
		requestJson.put("image_thumbnail_process", "image/resize,w_256/format,jpeg");
		requestJson.put("image_url_process", "image/resize,w_1920/format,jpeg");
		requestJson.put("limit", 100);
		requestJson.put("url_expire_sec", 14400);
		requestJson.put("order_by", "name");
		requestJson.put("order_direction", "ASC");
		requestJson.put("parent_file_id", fileId);
		requestJson.put("video_thumbnail_process", "video/snapshot,t_1000,f_jpg,ar_auto,w_256");

		Map<String, String> headerMap = new HashMap<>();
		headerMap.put("Content-Type", "application/json");
		headerMap.put("Authorization", "Bearer " + Constants.ACCESS_TOKEN_OPEN);

		String result = HttpClientUtil.doPost(openApiUrl + "/adrive/v1.0/openFile/list", requestJson.toString(), headerMap);
		JSONObject jsonObject = JSONObject.parseObject(result);
		//如果请求到json体不是空且不可用就刷新token
		if (jsonObject.getJSONArray("items") == null) {
			System.out.println(result + " " + DateUtil.date());
		}

		if (jsonObject.get("code") != null
				&& (("AccessTokenInvalid".equals(jsonObject.get("code")))
				|| ("AccessTokenExpired".equals(jsonObject.get("code")))
				|| ("ForbiddenDriveNotValid".equals(jsonObject.get("code"))))) {
			refresh();
			System.out.println(oauthRefreshToken());
			headerMap.put("Authorization", "Bearer " + Constants.ACCESS_TOKEN_OPEN);
			requestJson.put("drive_id", Constants.DEFAULT_DRIVE_ID);
			result = HttpClientUtil.doPost(openApiUrl + "/adrive/v1.0/openFile/list", requestJson.toString(), headerMap);
			jsonObject = JSONObject.parseObject(result);
		}

		ArrayList<PanFile> panFiles = new ArrayList<>();
		// 成功返回了文件列表
		if (jsonObject.getJSONArray("items") != null) {
			JSONArray items = jsonObject.getJSONArray("items");
			for (int i = 0; i < items.size(); i++) {
				PanFile file = new PanFile();
				file.setFileId((String) items.getJSONObject(i).get("file_id"));
				file.setType((String) items.getJSONObject(i).get("type"));
				file.setName((String) items.getJSONObject(i).get("name"));
				file.setParentFileId((String) items.getJSONObject(i).get("parent_file_id"));
				file.setCreatedAt((String) items.getJSONObject(i).get("created_at"));
				file.setEncrypted(false);
				if ("file".equals(items.getJSONObject(i).get("type"))) {
					file.setPreviewUrl((String) items.getJSONObject(i).get("thumbnail"));
					file.setFileExtension((String) items.getJSONObject(i).get("file_extension"));
					file.setSize(((Number) items.getJSONObject(i).get("size")).longValue());
					file.setUrl((String) items.getJSONObject(i).get("url"));
				}
				panFiles.add(file);
			}
		}

		//文件列表中密码文件的位置
		int passwordIndex = -1;

		for (int i = 0; i < panFiles.size(); i++) {
			//列表中如果有
			if (passwordFileName.equals(panFiles.get(i).getName())) {
				//找到一个名字为password的文件，如果传参为空就说明没传密码，直接返回一个文件且encrypted为true
				if (password == null || "".equals(password)) {
					panFiles.clear();
					panFiles.add(new PanFile(passwordFileName, true, "file"));
					break;
				} else {
					//找到一个名字为password的文件，但是传了密码参数
					String folderPasswd = StringUtil.clearStr(HttpClientUtil.doGet(panFiles.get(i).getUrl(), null, new HashMap<>(), null));
					//如果密码没对上
					if (!password.equals(folderPasswd)) {
						panFiles.clear();
						panFiles.add(new PanFile(passwordFileName, true, "file"));
						break;
					} else {
						//密码对上了，在文件列表中删除这个密码文件(同一文件夹只能一个)
						 passwordIndex = i;
					}
				}
			}
		}
		if (passwordIndex != -1) {
			panFiles.remove(passwordIndex);
		}
		return panFiles;
	}

	/**
	 * 获取下载链接，过期4小时
	 *
	 * @param fileId
	 * @return
	 */
	@ResponseBody
	@PostMapping(value = "/getDownloadUrl/{fileId}")
	public PanFile getDownloadUrl(@PathVariable("fileId") String fileId) {

		JSONObject requestJson = new JSONObject();
		requestJson.put("drive_id", Constants.DEFAULT_DRIVE_ID);
		requestJson.put("file_id", fileId);
		requestJson.put("expire_sec", 14400);
		Map<String, String> headerMap = new HashMap<>();
		headerMap.put("Content-Type", "application/json");
		headerMap.put("Referer", refererURL);
		headerMap.put("Authorization", "Bearer " + Constants.ACCESS_TOKEN);

		String result = HttpClientUtil.doPost(apiUrl + "/file/get_download_url",
				requestJson.toString(), headerMap);
		JSONObject jsonObject = JSONObject.parseObject(result);

		PanFile file = new PanFile();
		file.setFileId(fileId);
		file.setSize(((Number) jsonObject.get("size")).longValue());
		file.setUrl((String) jsonObject.get("url"));
		return file;
	}

	/**
	 * 获取下载链接，过期4小时
	 *
	 * @param fileId
	 * @return
	 */
//	@GetMapping(value = "/d/{fileId}")
//	public String download(@PathVariable("fileId") String fileId) {
//
//		List<String> sign = SignUtil.sign(appId, Constants.DEVICE_ID, Constants.USER_ID, "0");
//		createSession(sign.get(0), sign.get(2));
//
//		JSONObject requestJson = new JSONObject();
//		requestJson.put("drive_id", Constants.DEFAULT_DRIVE_ID);
//		requestJson.put("file_id", fileId);
//		requestJson.put("expire_sec", 14400);
//		Map<String, String> headerMap = new HashMap<>();
//		headerMap.put("Content-Type", "application/json");
//		headerMap.put("Referer", refererURL);
//		headerMap.put("Authorization", "Bearer " + Constants.ACCESS_TOKEN);
//
//		headerMap.put("x-canary", "client=web,app=adrive,version=v3.17.0");
//		headerMap.put("x-device-id", Constants.DEVICE_ID);
//		headerMap.put("x-signature", sign.get(2));
//
//		String result = HttpClientUtil.doPost(apiUrl + "/file/get_download_url",
//				requestJson.toString(), headerMap);
//		JSONObject jsonObject = JSONObject.parseObject(result);
//		return "redirect:" + jsonObject.get("url");
//	}

	@GetMapping(value = "/d/{fileId}")
	public String downloadOpen(@PathVariable("fileId") String fileId) {

		JSONObject requestJson = new JSONObject();
		requestJson.put("drive_id", Constants.DEFAULT_DRIVE_ID);
		requestJson.put("file_id", fileId);
		requestJson.put("expire_sec", 14400);
		Map<String, String> headerMap = new HashMap<>();
		headerMap.put("Content-Type", "application/json");
		headerMap.put("Referer", refererURL);
		headerMap.put("Authorization", "Bearer " + Constants.ACCESS_TOKEN_OPEN);

		String result = HttpClientUtil.doPost(openApiUrl + "/adrive/v1.0/openFile/getDownloadUrl",
				requestJson.toString(), headerMap);
//		System.out.println("下载结果：" + result);
		JSONObject jsonObject = JSONObject.parseObject(result);

		if (jsonObject != null) {
			String code = jsonObject.getString("code");
			if ("TooManyRequests".equals(code)) {
				return "redirect:" + "/toomany";
			} else {
				if (jsonObject.getString("url") != null) {
					return "redirect:" + jsonObject.getString("url");
				}
			}
		}
		return "redirect:" + "/toomany";
	}

	private void createSession(String publicKey, String signature) {
		Map<String, String> headers = new HashMap<>();
		headers.put("Authorization", "Bearer " + Constants.ACCESS_TOKEN);
		headers.put("Content-Type", "application/json");
		headers.put("Referer", "https://www.aliyundrive.com");
		headers.put("x-canary", "client=web,app=adrive,version=v3.17.0");
		headers.put("x-device-id", Constants.DEVICE_ID);
		headers.put("x-signature", signature);

		JSONObject param01 = new JSONObject();
		param01.put("deviceName", "Redmi");
		param01.put("modelName", "M2012K11AC");
		param01.put("nonce", "0");
		param01.put("pubKey", publicKey);

		String createSessionResult = HttpRequest.post("https://api.aliyundrive.com/users/v1/users/device/create_session")
				.addHeaders(headers)
				.body(param01.toJSONString())
				.execute()
				.body();
//		System.out.println("create_session: " + createSessionResult);
	}

	/**
	 * 获取Office文件在线预览url和token
	 * @see <a href="https://help.aliyun.com/document_detail/396287.html">阿里云智能媒体管理</a>
	 *
	 * @param fileId
	 * @return
	 */
	@ResponseBody
	@PostMapping(value = "/getOfficePreviewUrl/{fileId}")
	public Map<String, String> getOfficePreviewUrl(@PathVariable("fileId") String fileId) {

		JSONObject requestJson = new JSONObject();
		requestJson.put("drive_id", Constants.DEFAULT_DRIVE_ID);
		requestJson.put("file_id", fileId);
		Map<String, String> headerMap = new HashMap<>();
		headerMap.put("Content-Type", "application/json");
		headerMap.put("Referer", refererURL);
		headerMap.put("Authorization", "Bearer " + Constants.ACCESS_TOKEN);

		String result = HttpClientUtil.doPost(apiUrl + "/file/get_office_preview_url",
				requestJson.toString(), headerMap);
		JSONObject jsonObject = JSONObject.parseObject(result);
		String previewUrl = "";
		String accessToken = "";
		if (jsonObject != null) {
			previewUrl = (String) jsonObject.get("preview_url");
			accessToken = (String) jsonObject.get("access_token");
		}
		Map<String, String> map = new HashMap();
		map.put("preview_url", previewUrl);
		map.put("access_token", accessToken);
		return map;
	}

	/**
	 * 刷新token
	 *
	 * @return
	 */
	@ResponseBody
	@GetMapping(value = "/refresh")
	public String refresh() {
		System.out.println("刷新refresh_token: " + DateUtil.date());
		String s = FileUtil.textFileToString(new File(System.getProperty("user.dir") +
				File.separator + "AliPanConfig"));
		Constants.setRefreshToken(s);
		JSONObject requestJson = new JSONObject();
		requestJson.put("grant_type", "refresh_token");
		requestJson.put("refresh_token", Constants.getRefreshToken());

		String result = HttpClientUtil.doPost(apiUrl + "/account/token", requestJson.toString());
		JSONObject jsonObject = JSONObject.parseObject(result);
		if (jsonObject.get("code") != null) {
			return "确认配置文件 AliPanConfig 首行是否为你的 refresh_token！";
		}

		if (jsonObject.get("code") == null && jsonObject.get("access_token") != null) {
			//刷新一次refresh_token到AliPanConfig
			String refreshToken = (String) jsonObject.get("refresh_token");
			if (refreshToken != null  && !"".equals(refreshToken)) {
				FileUtil.stringToTextFile(refreshToken, new File(System.getProperty("user.dir") +
						File.separator + "AliPanConfig"));
			}
			//更新一次access_token到Constants
			Constants.setAccessToken((String) jsonObject.get("access_token"));
			Constants.setRefreshToken(refreshToken);
			Constants.setDefaultDriveId((String) jsonObject.get("default_drive_id"));
			return "刷新配置文件成功，刷新 access_token 成功！";
		}
		return "其它问题，联系软件作者。";
	}

	/**
	 * 刷新token(open)
	 *
	 * @return
	 */
	@ResponseBody
	@GetMapping(value = "/oauthRefreshToken")
	public String oauthRefreshToken() {
		File configFile = new File(System.getProperty("user.dir") +
				File.separator + "AliPanConfigOpen");
		String s = FileUtil.textFileToString(configFile);
		if (StringUtil.isEmpty(s)) {
			return "确认配置文件 AliPanConfig 首行是否为你的 refresh_token！";
		}
		Constants.setRefreshTokenOpen(s);
		JSONObject requestJson = new JSONObject();
		requestJson.put("client_id", "");
		requestJson.put("client_secret", "");
		requestJson.put("grant_type", "refresh_token");
		requestJson.put("refresh_token", Constants.getRefreshTokenOpen());

		String result = HttpClientUtil.doPost(oauthTokenUrl, requestJson.toString());
		if (StringUtil.isEmpty(result)) {
			return "获取refresh_token失败，检查接口：" + oauthTokenUrl;
		}
		JSONObject jsonObject = JSONObject.parseObject(result);
		if (!StringUtil.isEmpty(jsonObject.getString("code")) && "Too Many Requests".equals(jsonObject.getString("code"))) {
			return "请求刷新Token接口频率过快 Too Many Requests";
		}
		if (!StringUtil.isEmpty(jsonObject.getString("access_token"))) {
			//刷新一次refresh_token到AliPanConfig
			String refreshToken = jsonObject.getString("refresh_token");
			if (!StringUtil.isEmpty(refreshToken)) {
				FileUtil.stringToTextFile(refreshToken, configFile);
			}
			//更新一次access_token到Constants
			Constants.setAccessTokenOpen(jsonObject.getString("access_token"));
			Constants.setRefreshTokenOpen(refreshToken);
			//设置default_drive_id
			Map<String, String> headers = new HashMap<>();
			headers.put("Authorization", "Bearer " + jsonObject.getString("access_token"));
			String driveResult = HttpClientUtil.doPost(openApiUrl + "/adrive/v1.0/user/getDriveInfo", null, headers);
			JSONObject jsonDriveObject = JSONObject.parseObject(driveResult);
			Constants.setDefaultDriveId(jsonDriveObject.getString("default_drive_id"));
			return "刷新配置文件成功，刷新 access_token 成功！";
		}
		return "其它问题，联系软件作者。";
	}
}