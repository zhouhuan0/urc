package com.yks.urc.service.api;

import com.yks.urc.vo.ResultVO;
import com.yks.urc.vo.UserVO;

public interface IOrganizationService {
	
	
	/**
	 * 得到所有部门的json树
	 * @return
	 */
	ResultVO getAllOrgTree();

	/**
	 *  精确搜索用户
	 * @return
	 */
	ResultVO getUserByUserName(String operator, UserVO userVo);
	
	

}
