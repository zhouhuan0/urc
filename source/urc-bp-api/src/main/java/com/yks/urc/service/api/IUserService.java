package com.yks.urc.service.api;

import com.yks.urc.vo.NullVO;
import com.yks.urc.vo.ResultVO;
import com.yks.urc.vo.UserVO;

public interface IUserService {
	ResultVO syncUserInfo(UserVO curUser);

	ResultVO login(UserVO curUser, UserVO authUser);
}
