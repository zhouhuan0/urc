package com.yks.urc.service.api;

import com.yks.urc.entity.DataRuleDO;
import com.yks.urc.vo.*;

import java.util.List;
import java.util.Map;

public interface IUserService {
	ResultVO syncUserInfo(UserVO curUser);

	ResultVO login(UserVO authUser);

/*	*//**
	 * 根据数据dataRuleId获取用户名
	 * @param ruleDO
	 * @return
	 *//*
	ResultVO queryUserDataByRuleId(DataRuleDO ruleDO);
	
	*//**
	 * 根据数据没有dataRuleId获取用户名
	 * @param ruleDO
	 * @return
	 *//*
	ResultVO queryUserNoDataByRuleId(DataRuleDO ruleDO);
	*/
	
	/**
	 * 用户管理搜索用户
	 * @param userVO
	 * @param pageNumber
	 * @param pageData
	 * @return
	 */
	ResultVO<PageResultVO> getUsersByUserInfo(String operator,UserVO userVO, int pageNumber, int pageData);
	/**
	 *  获取所有平台
	 * @param operator
	 * @return
	 */
	ResultVO<List<OmsPlatformVO>> getPlatformList(String operator);
	/**
	 *  获取指定平台下的账号,站点
	 * @param operator
	 * @param platform
	 * @return
	 */
	ResultVO<List<OmsAccountVO>> getShopList(String operator, String platform);

	/**
	 *  获取应用系统及其授权方式
	 * @param
	 * @return
	 * @Author linwanxian@youkeshu.com
	 * @Date 2018/6/14 17:42
	 */
	ResultVO<List<SysAuthWayVO>> getMyAuthWay(String operator);


	ResultVO<List<UserSysVO>> getAllFuncPermit(String jsonStr);

	ResultVO funcPermitValidate(Map<String, String> map);
	
	ResultVO getUserByName(String userName);
	
	
	/**
	 * 给用户分配角色
	 * @param userNmae
	 * @param roleId
	 * @return
	 */
	ResultVO disUserToRoles(String userName,List<String> roleId);
	
	
	/**
	 * 模糊搜索用户域账号
	 * @param userNmae
	 * @param roleId
	 * @return
	 */
	ResultVO fuzzySearchUsersByUserName(int pageNumber,int pageData,String userName,String operator);

}
