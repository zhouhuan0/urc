package com.yks.urc.service.api;

import com.yks.urc.vo.PersonVO;
import com.yks.urc.vo.ResultVO;

public interface IPersonService {
	
	
	/**
	 * 根据DingOrgId获取人员信息含有分页
	 * @return
	 */
	ResultVO getUserByDingOrgId(PersonVO person,int pageNumber, int pageData);
	
	
	/**
	 * 搜索用户全局
	 * @return
	 */
	ResultVO getUserByUserInfo(PersonVO person,int pageNumber, int pageData);
	
	
	
	/**
	 * 同步钉钉数据
	 * @param userName
	 * @return
	 */
	ResultVO SynPersonOrgFromDing(String userName);

}
