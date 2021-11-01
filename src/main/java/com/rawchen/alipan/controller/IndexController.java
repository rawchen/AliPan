package com.rawchen.alipan.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

	@RequestMapping("/")
	public String toIndexHtml(){
		System.out.println("Index启动");
//		return "redirect:"+"/folder2/root";
		return "forward:"+"/folder2/root";
	}
}
