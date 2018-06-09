package com.yks.demo.controller;

import com.yks.urc.motan.service.api.IUrcService;
import com.yks.urc.vo.ResultVO;
import com.yks.urc.vo.UserVO;

public class UserLoginRunnable implements Runnable {
	String _userName;
	String _pwd;
	IUrcService _urcService;
	public static volatile boolean isStop = false;

	public UserLoginRunnable(String userName, String pwd, IUrcService urcService) {
		_userName = userName;
		_urcService = urcService;
		_pwd = pwd;
	}

	private ResultVO loginTest(String userName, String pwd) {
		UserVO curUser = new UserVO();
		curUser.ip = "127.0.0.1";

		UserVO authUser = new UserVO();
		authUser.userName = userName;
		authUser.pwd = pwd;
		return _urcService.login(curUser, authUser);
	}

	@Override
	public void run() {
		// Thread.currentThread().setName(String.format("%s-%s", _userNamePrefix,
		// number.get()));
		// String strUserName = String.format("%s-%s", _userNamePrefix,
		// Thread.currentThread().getName());
		while (!isStop) {
			try {
				ResultVO r = loginTest(_userName, _pwd);
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
	}

}
