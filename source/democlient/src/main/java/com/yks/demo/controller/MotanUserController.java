package com.yks.demo.controller;

import com.weibo.api.motan.config.springsupport.annotation.MotanReferer;
import com.yks.urc.motan.service.api.IUrcService;
import com.yks.urc.vo.ResultVO;
import com.yks.urc.vo.UserVO;

import java.util.UUID;

import org.jboss.netty.util.internal.StringUtil;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MotanUserController {

	@MotanReferer
	private IUrcService urcService;

	@RequestMapping("/sayHello/{msg}")
	public ResultVO sayHello(@PathVariable String msg) {
		// ResultVO rslt = userService.syncUserInfo();
		// return "110";
		UserVO curUser = new UserVO();
		curUser.userName = "py";
		curUser.userName = "py_" + UUID.randomUUID().toString();
		return urcService.syncUserInfo(curUser);// .sayHello(msg);
	}

	@RequestMapping("/login/{userName}/{pwd}")
	public ResultVO login(@PathVariable String userName, @PathVariable String pwd) {
		UserVO curUser = new UserVO();
		curUser.ip = "127.0.0.1";

		UserVO authUser = new UserVO();
		authUser.userName = userName;
		authUser.pwd = pwd;
		return urcService.login(curUser, authUser);
	}
}
