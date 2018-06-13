package com.yks.urc.user.bp.api;

import com.yks.urc.vo.PageResultVO;
import com.yks.urc.vo.ResultVO;
import com.yks.urc.vo.UserVO;

public interface IUserBp {

	void SynUserFromUserInfo(String userName);

	ResultVO login(UserVO authUser);
	
	
	/**
	 * 用户管理搜索用户
	 * @param userVO
	 * @param pageNumber
	 * @param pageData
	 * @return
	 */
	ResultVO<PageResultVO> getUsersByUserInfo(UserVO userVO, int pageNumber, int pageData);

}
