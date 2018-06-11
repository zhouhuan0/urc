package com.yks.urc.motan.service.impl;

import com.yks.urc.motan.service.api.IUrcService;
import com.yks.urc.service.api.IRoleService;
import com.yks.urc.service.api.IUserService;
import com.yks.urc.vo.ResultVO;
import com.yks.urc.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;

public class UrcServiceImpl implements IUrcService {

	@Autowired
	private IUserService userService;

	@Autowired
	private IRoleService roleService;

	@Override
	public ResultVO syncUserInfo(UserVO curUser) {
		return userService.syncUserInfo(curUser);
	}

	@Override
	public ResultVO login(UserVO curUser, UserVO authUser) {
		return userService.login(curUser,authUser);
	}

}
