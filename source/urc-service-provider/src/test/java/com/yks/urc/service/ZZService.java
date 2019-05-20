package com.yks.urc.service;

import java.util.ArrayList;
import java.util.List;

import com.yks.urc.motan.MotanSession;
import com.yks.urc.motan.service.api.IUrcService;
import com.yks.urc.service.api.*;
import org.drools.compiler.lang.DRL5Expressions.literal_return;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.yks.common.enums.CommonMessageCodeEnum;
import com.yks.urc.dingding.client.DingApiProxy;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.vo.PersonVO;
import com.yks.urc.vo.ResultVO;
import com.yks.urc.vo.RoleVO;
import com.yks.urc.vo.helper.VoHelper;

import javax.management.relation.Role;


public class ZZService extends BaseServiceTest {
	
	private static Logger LOG = LoggerFactory.getLogger(ZZService.class);
	
	@Autowired
	private IOrganizationService orgService;
	
	@Autowired
	private IRoleService roleService;
	
	@Autowired
	private IPersonService personService;
	
	@Autowired
	private IUserService userService;
	@Autowired
    private IUrcService urcService;
	
	
	@Autowired
	private IPermissionService permissionService;
	
	@Autowired
	private IDataRuleService dataRuleService;

    @Autowired
    private ICsService csService;

	@Autowired
	private DingApiProxy dingApiProxy;

	@Test
	public void getPlatformShopByEntityCode() throws Exception{
		String jsonStr = "{\"entityCode\":\"E_PlsShopAccount\",\"lstSellerId\":[\"cn1525069322gjcn\",\"cn1525069561iluv\",\"123456\"],\"platformCode\":\"速卖通\"}";
		ResultVO resultVO=urcService.getPlatformCode(jsonStr);
        System.out.println(StringUtility.toJSONString(resultVO));
	}
	 
    
    
    
}
