package com.rawchen.alipan.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

	@Value("${alipan.parent_file_id}")
	String parentFileId;

	@Value("${alipan.password_file_name}")
	String passwordFileName;

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

	@RequestMapping("/token")
	public String token() {
		return "token";
	}
}
