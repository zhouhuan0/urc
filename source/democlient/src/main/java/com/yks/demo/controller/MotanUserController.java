package com.yks.demo.controller;

import com.weibo.api.motan.config.springsupport.annotation.MotanReferer;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.motan.service.api.IUrcService;
import com.yks.urc.vo.ResultVO;
import com.yks.urc.vo.UserVO;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.util.internal.StringUtil;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

	@RequestMapping("/sayHello2/{msg}")
	public String sayHello2(@PathVariable String msg) {
		// ResultVO rslt = userService.syncUserInfo();
		// return "110";
		return urcService.sayHello2(msg);// .sayHello(msg);
	}

	@RequestMapping("/login/{userName}/{pwd}")
	public ResultVO login(@PathVariable String userName, @PathVariable String pwd) {
		return loginTest(userName, pwd);
	}

	private ResultVO loginTest(String userName, String pwd) {
		UserVO curUser = new UserVO();
		curUser.ip = "127.0.0.1";

		UserVO authUser = new UserVO();
		authUser.userName = userName;
		authUser.pwd = pwd;
		return urcService.login(curUser, authUser);
	}

	private ExecutorService service = Executors.newCachedThreadPool(); // 创建一个线程池
	private AtomicInteger number = new AtomicInteger(0);

	@RequestMapping("/startRequest")
	public ResultVO startRequest(@RequestParam("c") int c) {
		ResultVO rslt = new ResultVO<>();
		UserLoginRunnable.init();
		String strUserName = String.format("%s", StringUtility.dt2Str(new Date(), StringUtility.DtFormatString_yyyyMMddHHmmss));
		rslt.msg = String.format("并发数:%s userName:%s pwd:%s", c, strUserName, strUserName);

		number.set(0);
		for (int i = 0; i < c; i++) {
			number.getAndIncrement();
			service.execute(new UserLoginRunnable(String.format("%s-%s", strUserName, number.get()), strUserName, urcService));
		}
		return rslt;
	}

	@RequestMapping("/stopRequest")
	public ResultVO stopRequest() {
		UserLoginRunnable.isStop = true;
		ResultVO rslt = new ResultVO<>();
		rslt.msg = String.format("%s", UserLoginRunnable.getStatStr());
		return rslt;
	}
}
