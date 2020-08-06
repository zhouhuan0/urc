package com.yks.urc.service.api;

import com.yks.urc.vo.PersonVO;
import com.yks.urc.vo.ResultVO;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.List;
import java.util.Map;

public interface IPersonService {
	
	
	/**
	 * 根据DingOrgId获取人员信息含有分页
	 * @return
	 */
	ResultVO getUserByDingOrgId(String dingOrgId,String pageNumber, String pageData);
	
	
	/**
	 * 搜索用户全局
	 * @return
	 */
	ResultVO getUserByUserInfo(PersonVO person,String pageNumber, String pageData);

	void saveDingDingInfo(Map<String, List> initInfo) throws Exception;
	
	/**
	 * 同步钉钉数据
	 * @param userName
	 * @return
	 */
	ResultVO SynPersonOrgFromDing(String userName);

	ResultVO pullAndSaveDingDingInfo() throws Exception;
	/**
	 * 通过名字模糊搜索用户信息
	 * @param
	 * @return
	 * @Author linwanxian@youkeshu.com
	 * @Date 2018/7/9 19:25
	 */
	ResultVO fuzzSearchPersonByName(String operator,String userName);


	ResultVO getDepartment(String orgLevel);

    ResultVO fuzzSearchPersonByName4Account(String operator, String userName, Integer exact, Integer pageData, Integer pageNum);
}
