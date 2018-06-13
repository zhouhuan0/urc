package com.yks.urc.service.api;

import com.yks.urc.entity.DataRuleDO;
import com.yks.urc.vo.NullVO;
import com.yks.urc.vo.PageResultVO;
import com.yks.urc.vo.ResultVO;
import com.yks.urc.vo.UserVO;

public interface IUserService {
	ResultVO syncUserInfo(UserVO curUser);

	ResultVO login(UserVO authUser);

	/**
	 * 根据数据dataRuleId获取用户名
	 * @param ruleDO
	 * @return
	 */
	ResultVO queryUserDataByRuleId(DataRuleDO ruleDO);
	
	/**
	 * 根据数据没有dataRuleId获取用户名
	 * @param ruleDO
	 * @return
	 */
	ResultVO queryUserNoDataByRuleId(DataRuleDO ruleDO);
	
	
	/**
	 * 用户管理搜索用户
	 * @param userVO
	 * @param pageNumber
	 * @param pageData
	 * @return
	 */
	ResultVO<PageResultVO> getUsersByUserInfo(UserVO userVO, int pageNumber, int pageData);

}
