package com.yks.urc.motan.service.impl;

import com.yks.urc.fw.StringUtility;
import com.yks.urc.motan.service.api.IUrcService;
import com.yks.urc.service.api.IRoleService;
import com.yks.urc.service.api.IUserService;
import com.yks.urc.vo.ResultVO;
import com.yks.urc.vo.UserVO;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

public class UrcServiceImpl implements IUrcService {

	@Autowired
	private IUserService userService;

	@Autowired
	private IRoleService roleService;

	@Override
	public String syncUserInfo(UserVO curUser) {
		return StringUtility.toJSONString_NoException(userService.syncUserInfo(curUser));
	}

	@Override
	public String login(Map<String, String> map) {
		UserVO authUser = new UserVO();
		authUser.userName = map.get("userName");
		authUser.pwd = map.get("pwd");
		authUser.ip = map.get("ip");
		// UserVO curUser, UserVO authUser
		return StringUtility.toJSONString_NoException(userService.login(authUser, authUser));
	}
}
