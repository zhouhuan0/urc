package com.yks.urc.service.api;

import com.yks.urc.vo.PersonVO;
import com.yks.urc.vo.ResultVO;

public interface IPersonService {
	
	
	/**
	 * 根据DingOrgId获取人员信息
	 * @return
	 */
	ResultVO getUserByDingOrgId(String dingOrgId);
	
	
	/**
	 * 搜索用户
	 * @return
	 */
	ResultVO getUserByUserInfo(PersonVO person);
	
	
	
	
	void SynPersonOrgFromDing(String userName);

}
