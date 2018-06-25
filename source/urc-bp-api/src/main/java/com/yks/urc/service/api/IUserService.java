package com.yks.urc.service.api;

import com.yks.urc.vo.*;

import java.util.List;
import java.util.Map;

public interface IUserService {
	ResultVO syncUserInfo(String curUser);

	ResultVO login(UserVO authUser);
	ResultVO logout(String jsonStr);

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
	ResultVO<PageResultVO> getUsersByUserInfo(String operator,UserVO userVO, String pageNumber, String pageData);
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
	ResultVO<List<OmsShopVO>> getShopList(String operator, String platform);


	/**
	 *  获取应用系统及其授权方式
	 * @param
	 * @return
	 * @Author linwanxian@youkeshu.com
	 * @Date 2018/6/14 17:42
	 */
	ResultVO<List<SysAuthWayVO>> getMyAuthWay(String operator);


	ResultVO<GetAllFuncPermitRespVO> getAllFuncPermit(String jsonStr);

	ResultVO funcPermitValidate(Map<String, String> map);
	
	ResultVO getUserByName(String userName);
	
	/**
	 * 模糊搜索用户域账号
	 * @param userNmae
	 * @param roleId
	 * @return
	 */
	ResultVO fuzzySearchUsersByUserName(String pageNumber,String pageData,String userName,String operator);
	/**
	 * 精确搜索用户
	 * @param   operator
	 * @param userVO
	 * @return
	 * @Author linwanxian@youkeshu.com
	 * @Date 2018/6/25 14:46
	 */
	ResultVO<List<UserVO>> getUserByUserName(String operator, UserVO userVO);
}
