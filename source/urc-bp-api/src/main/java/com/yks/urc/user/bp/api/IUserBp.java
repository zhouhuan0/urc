package com.yks.urc.user.bp.api;

import com.yks.urc.vo.ResultVO;
import com.yks.urc.vo.UserVO;

public interface IUserBp {

	void SynUserFromUserInfo(String userName);

	ResultVO login(UserVO authUser);

}
