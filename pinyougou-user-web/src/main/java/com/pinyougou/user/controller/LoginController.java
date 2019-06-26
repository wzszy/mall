package com.pinyougou.user.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;

@RestController
@RequestMapping("login")
public class LoginController {
	@Reference
	private UserService userService;
	@RequestMapping("name")
	public Map showName() {
		Map<String,String> map = new HashMap<>();
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		map.put("loginName", name);
		TbUser user = userService.findOneByUserName(name);
		map.put("headPic", user.getHeadPic());
		return map;
	}
}
