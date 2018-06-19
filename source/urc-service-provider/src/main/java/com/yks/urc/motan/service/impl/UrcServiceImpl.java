package com.yks.urc.motan.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.log.Log;
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
    public ResultVO syncUserInfo(UserVO curUser) {
        return userService.syncUserInfo(curUser);
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
    public ResultVO syncDingOrgAndUser() {
        return personService.SynPersonOrgFromDing("hand");
    }

    @Override
    public ResultVO getUserByDingOrgId(String params) {
        JSONObject jsonObject = StringUtility.parseString(params);
        int pageNumber = Integer.valueOf(jsonObject.get("pageNumber").toString());
        int pageData = Integer.valueOf(jsonObject.get("pageData").toString());
        String  dingOrgId = jsonObject.get("dingOrgId").toString();
        return personService.getUserByDingOrgId(dingOrgId, pageNumber, pageData);
    }

    @Override
    public ResultVO getUserByUserInfo(String params) {
        JSONObject jsonObject = StringUtility.parseString(params);
        int pageNumber = Integer.valueOf(jsonObject.get("pageNumber").toString());
        int pageData = Integer.valueOf(jsonObject.get("pageData").toString());
        PersonVO personVo = StringUtility.parseObject(jsonObject.get("user").toString(), PersonVO.class);
        return personService.getUserByUserInfo(personVo, pageNumber, pageData);
    }

    @Override
    public ResultVO getAllOrgTree() {
        return organizationService.getAllOrgTree();
    }

    /**
     *  用户管理搜索用户
     * @param params
     * @return
     */
	@Override
	public ResultVO<PageResultVO> getUsersByUserInfo(String params) {
        JSONObject jsonObject = StringUtility.parseString(params);
        String operator =StringUtility.toJSONString(jsonObject.getString("operator"));
        int pageNumber = Integer.valueOf(jsonObject.get("pageNumber").toString());
        int pageData = Integer.valueOf(jsonObject.get("pageData").toString());
        UserVO userVO = StringUtility.parseObject(jsonObject.get("user").toString(), UserVO.class);
		return userService.getUsersByUserInfo(operator,userVO, pageNumber, pageData);
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
    public ResultVO assignDataRuleTempl2User(String jsonStr) {
        return dataRuleService.assignDataRuleTempl2User(jsonStr);
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
    public ResultVO<DataRuleTemplVO> getDataRuleTemplByTemplId(String jsonStr) {
        return dataRuleService.getDataRuleTemplByTemplId(jsonStr);
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
    public ResultVO<PageResultVO> getDataRuleTempl(String jsonStr) {
        return dataRuleService.getDataRuleTempl(jsonStr);
    }

	@Override
	public ResultVO syncUserInfo() {
		UserVO curUser=new UserVO();
		curUser.userName="hand";
		return userService.syncUserInfo(curUser);
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
    @Log("角色名校重")
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
	public ResultVO getUserByRoleId(String jsonStr) {
        JSONObject jsonObject = StringUtility.parseString(jsonStr);
        String operator = jsonObject.get("operator").toString();
        String roleId= jsonObject.get("roleId").toString();
		return roleService.getUserByRoleId(operator,roleId);
	}

	@Override
	public ResultVO getRoleUser(String jsonStr) {
        JSONObject jsonObject = StringUtility.parseString(jsonStr);
        String operator = jsonObject.get("operator").toString();
        List<String> roleList = StringUtility.parseObject(jsonObject.get("lstRoleId").toString(), List.class);
		return roleService.getRoleUser(operator,roleList);
	}



	public ResultVO getMyDataRuleTempl(String jsonStr) {
        JSONObject jsonObject = StringUtility.parseString(jsonStr);
        String operator = jsonObject.get("operator").toString();
        int pageNumber = Integer.valueOf(jsonObject.get("pageNumber").toString());
        int pageData = Integer.valueOf(jsonObject.get("pageData").toString());
        return dataRuleService.getMyDataRuleTempl(pageNumber,pageData,operator);
	}


	public List<DataRuleVO> getDataRuleByUser(String jsonStr) {
		JSONObject jsonObject = StringUtility.parseString(jsonStr);
	    String operator = jsonObject.get("operator").toString();
		List<String> lstUserName = StringUtility.parseObject(jsonObject.get("lstUserName").toString(), List.class);
		return dataRuleService.getDataRuleByUser(lstUserName);
	}


	@Override
	public ResultVO importSysPermit(String jsonStr) {
		return permissionService.importSysPermit(jsonStr);
	}

	@Override
	public ResultVO getUserAuthorizablePermission(String jsonStr) {
        JSONObject jsonObject = StringUtility.parseString(jsonStr);
        String operator = jsonObject.get("operator").toString();
		return permissionService.getUserAuthorizablePermission(operator);
	}

	@Override
	public ResultVO getRolePermission(String jsonStr) {
        JSONObject jsonObject = StringUtility.parseString(jsonStr);
        String operator = jsonObject.get("operator").toString();
		List<String> lstRoleId = StringUtility.parseObject(jsonObject.get("lstRoleId").toString(), List.class);
		return roleService.getRolePermission(operator,lstRoleId);
	}

    @Override
    public ResultVO getUserByUserName(String jsonStr) {
        JSONObject jsonObject =StringUtility.parseString(jsonStr);
        String operator =jsonObject.get("operator").toString();
        UserVO userVO =StringUtility.parseObject(jsonObject.get("user").toString(),UserVO.class);
        return organizationService.getUserByUserName(operator,userVO);
    }

    @Override
    public ResultVO<List<SysAuthWayVO>> getMyAuthWay(String operator) {
        return userService.getMyAuthWay(operator);

    }

	@Override
	public ResultVO fuzzySearchUsersByUserName(String jsonStr) {
        JSONObject jsonObject = StringUtility.parseString(jsonStr);
        String operator = jsonObject.get("operator").toString();
        String userName = jsonObject.get("username").toString();
        int pageNumber = Integer.valueOf(jsonObject.get("pageNumber").toString());
        int pageData = Integer.valueOf(jsonObject.get("pageData").toString());
        return userService.fuzzySearchUsersByUserName(pageNumber, pageData, userName, operator);
	}

	
	
	@Override
	public ResultVO updateUsersOfRole(String jsonStr) {
        JSONObject jsonObject = StringUtility.parseString(jsonStr);
        String operator = jsonObject.get("operator").toString();
        List<RoleVO> lstRole =StringUtility.parseObject(jsonObject.get("lstRole").toString(),List.class);
		return roleService.updateUsersOfRole(lstRole, operator);
	}

}
