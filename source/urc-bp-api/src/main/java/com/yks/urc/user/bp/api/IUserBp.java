package com.yks.urc.user.bp.api;

import com.yks.urc.vo.PageResultVO;
import com.yks.urc.vo.ResultVO;
import com.yks.urc.vo.UserSysVO;
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
	ResultVO<PageResultVO> getUsersByUserInfo(String operator,UserVO userVO, int pageNumber, int pageData);
	/**
	 * 获取系统key
	 * @param
	 * @return
	 * @Author linwanxian@youkeshu.com
	 * @Date 2018/6/14 14:47
	 */
	ResultVO<UserSysVO> getSysKeyByUserName(String userName, String sysKey, String ticket);
}
