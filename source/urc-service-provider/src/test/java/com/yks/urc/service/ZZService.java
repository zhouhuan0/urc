package com.yks.urc.service;

import java.util.ArrayList;
import java.util.List;

import com.yks.urc.motan.MotanSession;
import com.yks.urc.motan.service.api.IUrcService;
import com.yks.urc.service.api.*;
import com.yks.urc.user.bp.api.IUrcLogBp;

import org.drools.compiler.lang.DRL5Expressions.literal_return;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yks.common.enums.CommonMessageCodeEnum;
import com.yks.urc.Enum.ModuleCodeEnum;
import com.yks.urc.dingding.client.DingApiProxy;
import com.yks.urc.entity.RoleDO;
import com.yks.urc.entity.UrcLog;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.mapper.IRoleMapper;
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
	 
	
	@Test
	public void testUrcLogApi(){
		String jsonStr = "{\"data\":{\"userName\":\"\"},\"operator\":\"zengzheng\"}";
		ResultVO resultVO=urcService.getUserName(jsonStr);
        System.out.println(StringUtility.toJSONString(resultVO));
        
        jsonStr = null;
        resultVO=urcService.getLogModuleList(jsonStr);
        
        System.out.println(StringUtility.toJSONString(resultVO));
        
        jsonStr = "{\"data\":{\"moduleCode\":\"1\",\"operateTimeRange\":[1560096000000, 1563206399000],\"pageData\":20,\"pageNumber\":1,\"userName\":\"chencanwei\"},\"operator\":\"zengzheng\"}";
        resultVO=urcService.getLogList(jsonStr);
        System.out.println(StringUtility.toJSONString(resultVO));
	}
	@Autowired
    private IRoleMapper roleMapper;
	@Autowired
    IUrcLogBp iUrcLogBp;
	@Test
	public void testUrcLog(){
		List<Long> roleIds = new ArrayList<>();
		roleIds.add(1539160322094000002L);
		List<RoleDO> roleDOs= roleMapper.getRoleByRoleIds(roleIds);
        List<String> roleNames = new ArrayList<>();
        roleDOs.forEach(c -> roleNames.add(c.getRoleName()));
      //保存操作日志
        UrcLog urcLog = new UrcLog("zengzheng", ModuleCodeEnum.ROLE_MANAGERMENT.getStatus(), "批量分配角色功能权限", String.format("%s->%s",roleNames,"zengzheng"), JSON.toJSONString(roleNames));
        iUrcLogBp.insertUrcLog(urcLog);
	}
    
    
}
