package com.yks.urc.motan.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yks.urc.entity.Person;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.motan.service.api.IUrcService;
import com.yks.urc.service.api.IOrganizationService;
import com.yks.urc.service.api.IPersonService;
import com.yks.urc.service.api.IRoleService;
import com.yks.urc.service.api.IUserService;
import com.yks.urc.vo.DataRuleTemplVO;
import com.yks.urc.vo.PersonVO;
import com.yks.urc.vo.ResultVO;
import com.yks.urc.vo.UserVO;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

public class UrcServiceImpl implements IUrcService {

	@Autowired
	private IUserService userService;

	@Autowired
	private IRoleService roleService;
	
	@Autowired
	private IPersonService personService;
	
	
	@Autowired
	private IOrganizationService organizationService;

	@Override
	public String syncUserInfo(UserVO curUser) {
		return StringUtility.toJSONString_NoException(userService.syncUserInfo(curUser));
	}

	@Override
	public String login(Map<String, String> map) {
		UserVO authUser = new UserVO();
		authUser.userName = map.get("userName");
		authUser.pwd = map.get("pwd");
		authUser.ip = map.get("ip");
		// UserVO curUser, UserVO authUser
		return StringUtility.toJSONString_NoException(userService.login(authUser));
	}
	
	@Override
	public String syncDingOrgAndUser() {
		return StringUtility.toJSONString_NoException(personService.SynPersonOrgFromDing("hand"));
	}

	@Override
	public String getUserByDingOrgId(String params) {
        JSONObject jsonObject = StringUtility.parseString(params);
        int pageNumber = Integer.valueOf(jsonObject.get("pageNumber").toString());
        int pageData = Integer.valueOf(jsonObject.get("pageData").toString());
        PersonVO personVo = StringUtility.parseObject(jsonObject.get("templ").toString(), PersonVO.class);
		return StringUtility.toJSONString_NoException(personService.getUserByDingOrgId(personVo, pageNumber, pageData));
	}

	@Override
	public String getUserByUserInfo(String params) {
        JSONObject jsonObject = StringUtility.parseString(params);
        int pageNumber = Integer.valueOf(jsonObject.get("pageNumber").toString());
        int pageData = Integer.valueOf(jsonObject.get("pageData").toString());
        PersonVO personVo = StringUtility.parseObject(jsonObject.get("templ").toString(), PersonVO.class);
		return StringUtility.toJSONString_NoException(personService.getUserByUserInfo(personVo, pageNumber, pageData));
	}

	@Override
	public String getAllOrgTree() {
		return StringUtility.toJSONString_NoException(organizationService.getAllOrgTree());
	}
	
	
	
	
}
