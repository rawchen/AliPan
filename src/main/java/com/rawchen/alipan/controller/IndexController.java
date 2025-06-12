package com.rawchen.alipan.controller;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

	@Value("${alipan.parent_file_id}")
	String parentFileId;

	@Value("${alipan.password_file_name}")
	String passwordFileName;

	private final String mobileLoginTokenApi = "https://passport.aliyundrive.com/newlogin/qrcode/generate.do?appName=aliyun_drive&isMobile=true";

	@RequestMapping("/")
	public String toIndexHtml(Model model) {
		model.addAttribute("type", "folder");
		model.addAttribute("fileId", parentFileId);
		model.addAttribute("passwordFileName", passwordFileName);
		return "index";
	}

	@RequestMapping("/toomany")
	public String toomany(Model model) {
		return "toomany";
	}

	@RequestMapping("/folder/{fileId}")
	public String toFolder(@PathVariable("fileId") String fileId, Model model) {
		model.addAttribute("fileId", fileId);
		model.addAttribute("type", "folder");
		model.addAttribute("passwordFileName", passwordFileName);
		return "index";
	}

	@RequestMapping("/file/{fileId}")
	public String toFile(@PathVariable("fileId") String fileId, Model model) {
		model.addAttribute("fileId", fileId);
		model.addAttribute("type", "file");
		model.addAttribute("parentFileId", parentFileId);
		model.addAttribute("passwordFileName", passwordFileName);
		return "index";
	}

	@RequestMapping("/open_token")
	public String toToken() {
		return "redirect:" + "https://www.alipan.com/o/oauth/authorize?client_id=432fe7ab15fd4ce7bc27c1c407eab9a9&redirect_uri=https%3A%2F%2Fpan.rawchen.com%2Fcallback&scope=user:base,file:all:read,file:all:write&state=Ojo=&response_type=code&relogin=true";
	}

	@GetMapping("/original_token")
	public String alipan(Model model) {
		HttpRequest request = HttpUtil.createGet(mobileLoginTokenApi);
		HttpResponse response = request.execute();
		JSONObject jsonObject = JSONObject.parseObject(response.body());
		JSONObject content = (JSONObject) jsonObject.get("content");
		JSONObject data = (JSONObject) content.get("data");
		String codeContent = data.getString("codeContent");
		String t = data.getString("t");
		String ck = data.getString("ck");
		model.addAttribute("codeContent", codeContent);
		model.addAttribute("qrcode", "https://api.rawchen.com/api/qrcode?url=" + codeContent);
		model.addAttribute("t", t);
		model.addAttribute("ck", ck);
		return "original_token";
	}
}
