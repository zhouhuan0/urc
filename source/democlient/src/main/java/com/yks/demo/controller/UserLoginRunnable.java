package com.yks.demo.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.yks.urc.fw.StringUtility;
import com.yks.urc.motan.service.api.IUrcService;
import com.yks.urc.vo.ResultVO;
import com.yks.urc.vo.UserVO;

public class UserLoginRunnable implements Runnable {
	String _userName;
	String _pwd;
	IUrcService _urcService;
	public static volatile boolean isStop = false;

	private static Map<String, ThdInfo> map = new HashMap<>();

	public static void init() {
		isStop = false;
		map.clear();
	}

	public static String getStatStr() {
		long totalTime = 0L;
		long totalRequest = 0L;
		long avgRespTime = 0L;
		long totalSuccessRequest = 0L;
		long totalFailedRequest = 0L;
		long startTime = 0L;
		long endTime = 0L;
		StringBuilder sbFailedMsg = new StringBuilder();
		Iterator<String> it = map.keySet().iterator();
		while (it.hasNext()) {
			ThdInfo thd = map.get(it.next());
			totalTime += thd.totalTime;
			totalRequest += thd.totalRequest;
			totalSuccessRequest += thd.successRequest;
			totalFailedRequest += thd.failRequest;
			if (startTime == 0 || thd.startTime < startTime)
				startTime = thd.startTime;
			if (endTime == 0 || endTime < thd.endTime)
				endTime = thd.endTime;
			if (thd.failedMsg.length() > 0) {
				sbFailedMsg.append(thd.failedMsg);
				sbFailedMsg.append(StringUtility.NewLine());
			}
		}
		if (totalRequest > 0) {
			avgRespTime = totalTime / totalRequest;
		}

		return String.format("并发数:%s 运行总耗时：%s 运行起止时间:【%s - %s】总请求数：%s 成功:%s 出错:%s 总请求耗时：%s 平均响应时间：%s\r\nErrMsg:\r\n%s", map.size(), endTime - startTime, StringUtility.getDateTime_yyyyMMddHHmmssSSS(new Date(startTime)), StringUtility.getDateTime_yyyyMMddHHmmssSSS(new Date(endTime)), totalRequest, totalSuccessRequest, totalFailedRequest, totalTime, avgRespTime, sbFailedMsg);
	}

	public UserLoginRunnable(String userName, String pwd, IUrcService urcService) {
		_userName = userName;
		_urcService = urcService;
		_pwd = pwd;
		ThdInfo thdInfo = new ThdInfo();
		thdInfo.thdName = userName;
		thdInfo.totalRequest = 0L;
		thdInfo.successRequest = 0L;
		thdInfo.failRequest = 0L;
		thdInfo.totalTime = 0L;
		thdInfo.failedMsg = new StringBuilder();
		map.put(userName, thdInfo);
	}

	private ResultVO loginTest(String userName, String pwd) {
		UserVO curUser = new UserVO();
		curUser.ip = "127.0.0.1";

		UserVO authUser = new UserVO();
		authUser.userName = userName;
		authUser.pwd = pwd;
		authUser.ip = curUser.ip;
		return _urcService.login(StringUtility.toJSONString_NoException(authUser));
	}

	@Override
	public void run() {
		// Thread.currentThread().setName(String.format("%s-%s", _userNamePrefix,
		// number.get()));
		// String strUserName = String.format("%s-%s", _userNamePrefix,
		// Thread.currentThread().getName());
		ThdInfo thd = map.get(_userName);
		thd.startTime = System.currentTimeMillis();
		long startTime = 0L;
		long endTime = 0L;
		while (!isStop) {
			try {
				thd.totalRequest++;
				startTime = System.currentTimeMillis();
				ResultVO r = loginTest(_userName, _pwd);
				endTime = System.currentTimeMillis();
				thd.totalTime += (endTime - startTime);
				thd.successRequest++;
				Thread.sleep(100);
			} catch (Exception ex) {
				thd.failedMsg.append("耗时:");
				thd.failedMsg.append(System.currentTimeMillis() - startTime);
				thd.failedMsg.append(" ");
				thd.failedMsg.append(ex.getMessage());
				thd.failedMsg.append(StringUtility.NewLine());
				thd.failRequest++;
			}
		}
		thd.endTime = System.currentTimeMillis();
	}

}
