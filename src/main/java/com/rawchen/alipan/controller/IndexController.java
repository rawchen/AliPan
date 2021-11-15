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

	@RequestMapping("/")
	public String toIndexHtml(Model model){
		System.out.println("Index启动");
//		return "redirect:"+"/folder2/root";
//		return "forward:"+"/down/" + ;
		model.addAttribute("type", "folder");
		model.addAttribute("fileId", parentFileId);
		return "index";

	}

	@RequestMapping("/folder/{fileId}")
	public String toFolder(@PathVariable("fileId") String fileId, Model model){
		model.addAttribute("fileId", fileId);
		model.addAttribute("type", "folder");
		return "index";
	}

	@RequestMapping("/file/{fileId}")
	public String toFile(@PathVariable("fileId") String fileId, Model model){
		model.addAttribute("fileId", fileId);
		model.addAttribute("type", "file");
		return "index";
	}

	@RequestMapping("/token")
	public String token(){
		return "token";
	}
}
