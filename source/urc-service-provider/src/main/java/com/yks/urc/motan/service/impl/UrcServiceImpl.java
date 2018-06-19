package com.yks.urc.motan.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yks.urc.entity.DataRuleDO;
import com.yks.urc.entity.Person;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.mapper.IDataRuleTemplMapper;
import com.yks.urc.motan.service.api.IUrcService;
import com.yks.urc.service.api.*;
import com.yks.urc.vo.*;

import java.util.List;
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
    private IDataRuleTemplMapper dataRuleTemplMapper;


    @Autowired
    private IOrganizationService organizationService;

    @Autowired
    private IDataRuleService dataRuleService;

	@Autowired
	private IPermissionService permissionService;

    @Override
    public String syncUserInfo(UserVO curUser) {
        return StringUtility.toJSONString_NoException(userService.syncUserInfo(curUser));
    }

    @Override
    public ResultVO<LoginRespVO> login(Map<String, String> map) {
        UserVO authUser = new UserVO();
        authUser.userName = map.get("userName");
        authUser.pwd = map.get("pwd");
        authUser.ip = map.get("ip");
        // UserVO curUser, UserVO authUser
        return userService.login(authUser);
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
        String  dingOrgId = jsonObject.get("dingOrgId").toString();
        return StringUtility.toJSONString_NoException(personService.getUserByDingOrgId(dingOrgId, pageNumber, pageData));
    }

    @Override
    public String getUserByUserInfo(String params) {
        JSONObject jsonObject = StringUtility.parseString(params);
        int pageNumber = Integer.valueOf(jsonObject.get("pageNumber").toString());
        int pageData = Integer.valueOf(jsonObject.get("pageData").toString());
        PersonVO personVo = StringUtility.parseObject(jsonObject.get("user").toString(), PersonVO.class);
        return StringUtility.toJSONString_NoException(personService.getUserByUserInfo(personVo, pageNumber, pageData));
    }

    @Override
    public String getAllOrgTree() {
        return StringUtility.toJSONString_NoException(organizationService.getAllOrgTree());
    }

    
	@Override
	public String getUsersByUserInfo(String params) {
        JSONObject jsonObject = StringUtility.parseString(params);
        String operator =StringUtility.toJSONString(jsonObject.getString("operator"));
        int pageNumber = Integer.valueOf(jsonObject.get("pageNumber").toString());
        int pageData = Integer.valueOf(jsonObject.get("pageData").toString());
        UserVO userVO = StringUtility.parseObject(jsonObject.get("userVo").toString(), UserVO.class);
		return  StringUtility.toJSONString_NoException(userService.getUsersByUserInfo(operator,userVO, pageNumber, pageData));
	}

	
	
    /**
     * Description: 快速分配数据权限模板给用户
     *
     * @param :
     * @return:
     * @auther: lvcr
     * @date: 2018/6/13 13:53
     * @see
     */
    @Override
    public String assignDataRuleTempl2User(String jsonStr) {
        return StringUtility.toJSONString_NoException(dataRuleService.assignDataRuleTempl2User(jsonStr));
    }

    /**
     * Description: 根据templId获取数据权限模板
     *
     * @param :jsonStr
     * @return: String
     * @auther: lvcr
     * @date: 2018/6/13 13:55
     * @see
     */
    @Override
    public String getDataRuleTemplByTemplId(String jsonStr) {
        return StringUtility.toJSONString_NoException(dataRuleService.getDataRuleTemplByTemplId(jsonStr));
    }

    /**
     * Description: 获取数据权限模板
     *
     * @param :jsonStr
     * @return: String
     * @auther: lvcr
     * @date: 2018/6/13 13:56
     * @see
     */
    @Override
    public String getDataRuleTempl(String jsonStr) {
        return StringUtility.toJSONString_NoException(dataRuleService.getDataRuleTempl(jsonStr));
    }

	@Override
	public String syncUserInfo() {
		UserVO curUser=new UserVO();
		curUser.userName="hand";
		return StringUtility.toJSONString_NoException(userService.syncUserInfo(curUser));
	}

    @Override
    public ResultVO<List<OmsPlatformVO>> getPlatformList(String operator) {
        return userService.getPlatformList(operator);
    }

    @Override
    public ResultVO<List<OmsAccountVO>> getShopList(String operator, String platform) {
        return userService.getShopList(operator, platform);
    }

    @Override
    public ResultVO<Integer> checkDuplicateRoleName(String operator, String newRoleName, String roleId) {
        return roleService.checkDuplicateRoleName(operator, newRoleName, roleId);
    }

	@Override
	public ResultVO<List<UserSysVO>> getAllFuncPermit(String jsonStr) {
		return userService.getAllFuncPermit(jsonStr);
	}

    @Override
	public ResultVO funcPermitValidate(Map<String, String> map) {
		return userService.funcPermitValidate(map);
	}

	@Override
	public String getUserByRoleId(String jsonStr) {
        JSONObject jsonObject = StringUtility.parseString(jsonStr);
        String operator = jsonObject.get("operator").toString();
        String roleId= jsonObject.get("roleId").toString();
		return StringUtility.toJSONString_NoException(roleService.getUserByRoleId(roleId));
	}

	@Override
	public String getRoleUser(String jsonStr) {
        JSONObject jsonObject = StringUtility.parseString(jsonStr);
        String operator = jsonObject.get("operator").toString();
        List<String> roleList = StringUtility.parseObject(jsonObject.get("lstRoleId").toString(), List.class);
		return StringUtility.toJSONString_NoException(roleService.getRoleUser(roleList));
	}



	public String getMyDataRuleTempl(String jsonStr) {
        JSONObject jsonObject = StringUtility.parseString(jsonStr);
        String operator = jsonObject.get("operator").toString();
        int pageNumber = Integer.valueOf(jsonObject.get("pageNumber").toString());
        int pageData = Integer.valueOf(jsonObject.get("pageData").toString());
        return StringUtility.toJSONString_NoException(dataRuleService.getMyDataRuleTempl(pageNumber,pageData,operator));
	}


	public String getDataRuleByUser(String jsonStr) {
		JSONObject jsonObject = StringUtility.parseString(jsonStr);
	    String operator = jsonObject.get("operator").toString();
		List<String> lstUserName = StringUtility.parseObject(jsonObject.get("lstUserName").toString(), List.class);
		return StringUtility.toJSONString_NoException(dataRuleService.getDataRuleByUser(lstUserName));
	}


	@Override
	public String importSysPermit(String jsonStr) {
		return permissionService.importSysPermit(jsonStr);
	}

	@Override
	public String getUserAuthorizablePermission(String jsonStr) {
        JSONObject jsonObject = StringUtility.parseString(jsonStr);
        String operator = jsonObject.get("operator").toString();
		return StringUtility.toJSONString_NoException(permissionService.getUserAuthorizablePermission(operator));
	}

	@Override
	public String getRolePermission(String jsonStr) {
        JSONObject jsonObject = StringUtility.parseString(jsonStr);
        String operator = jsonObject.get("operator").toString();
		List<String> lstRoleId = StringUtility.parseObject(jsonObject.get("lstRoleId").toString(), List.class);
		return StringUtility.toJSONString_NoException(roleService.getRolePermission(lstRoleId));
	}

    @Override
    public String getMyAuthWay(String operator) {
        return StringUtility.toJSONString_NoException(userService.getMyAuthWay(operator));
    }

	@Override
	public String getUserByUserName(String jsonStr) {
        JSONObject jsonObject = StringUtility.parseString(jsonStr);
        String userName = jsonObject.get("userName").toString();
		return StringUtility.toJSONString_NoException(userService.getUserByName(userName));
	}
}
