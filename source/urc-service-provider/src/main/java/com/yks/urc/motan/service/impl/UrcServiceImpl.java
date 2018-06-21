package com.yks.urc.motan.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.log.Log;
import com.yks.urc.log.LogLevel;
import com.yks.urc.mapper.IDataRuleTemplMapper;
import com.yks.urc.motan.service.api.IUrcService;
import com.yks.urc.operation.bp.api.IOperationBp;
import com.yks.urc.service.api.*;
import com.yks.urc.vo.*;

import java.util.List;
import java.util.Map;

import com.yks.urc.vo.helper.VoHelper;
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

    @Autowired
    private IOperationBp operationBp;

    @Override
    @Log(value = "同步数据",level = LogLevel.INFO)
    public ResultVO syncUserInfo(String json) {
        JSONObject jsonObject = StringUtility.parseString(json);
        String operator = StringUtility.toJSONString(jsonObject.getString("operator"));
        return userService.syncUserInfo(operator);
    }

    @Override
	@Log(value = "login", level = LogLevel.ERROR)
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
        String dingOrgId = jsonObject.get("dingOrgId").toString();
        return personService.getUserByDingOrgId(dingOrgId, pageNumber, pageData);
    }

    @Override
    public ResultVO getUserByUserInfo(String params) {
        JSONObject jsonObject = StringUtility.parseString(params);
        int pageNumber = Integer.valueOf(jsonObject.get("pageNumber").toString());
        int pageData = Integer.valueOf(jsonObject.get("pageData").toString());
        PersonVO personVo = StringUtility.parseObject(jsonObject.getJSONObject("user").toString(), PersonVO.class);
        return personService.getUserByUserInfo(personVo, pageNumber, pageData);
    }

    @Override
    public ResultVO getAllOrgTree() {
        return organizationService.getAllOrgTree();
    }

    /**
     * 用户管理搜索用户
     *
     * @param params
     * @return
     */
    @Override
    @Log(value = "搜索用户",level = LogLevel.INFO)
    public ResultVO<PageResultVO> getUsersByUserInfo(String params) {
        JSONObject jsonObject = StringUtility.parseString(params);
        String operator = StringUtility.toJSONString(jsonObject.getString("operator"));
        int pageNumber = Integer.valueOf(jsonObject.get("pageNumber").toString());
        int pageData = Integer.valueOf(jsonObject.get("pageData").toString());
        UserVO userVO = StringUtility.parseObject(jsonObject.getJSONObject("user").toString(), UserVO.class);
        return userService.getUsersByUserInfo(operator, userVO, pageNumber, pageData);
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
    public ResultVO<List<OmsPlatformVO>> getPlatformList(String jsonStr) {
        JSONObject jsonObject =StringUtility.parseString(jsonStr);
        String operator = jsonObject.get("operator").toString();
        return userService.getPlatformList(operator);
    }

    @Override
    public ResultVO<List<OmsAccountVO>> getShopList(String jsonStr) {
        JSONObject jsonObject =StringUtility.parseString(jsonStr);
        String operator = jsonObject.get("operator").toString();
        String platform = jsonObject.get("platform").toString();
        return userService.getShopList(operator, platform);
    }

    @Override
    @Log("角色名校重")
    public ResultVO<Integer> checkDuplicateRoleName(String jsonStr) {
        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
        String operator = jsonObject.getString("operator");
        String newRoleName = jsonObject.getString("newRoleName");
        String roleId = jsonObject.getString("roleId");
        return roleService.checkDuplicateRoleName(operator, newRoleName, roleId);
    }

    @Override
    public ResultVO copyRole(String jsonStr) {
        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
        String operator = jsonObject.getString("operator");
        String newRoleName = jsonObject.getString("newRoleName");
        String sourceRoleId = jsonObject.getString("sourceRoleId");
        roleService.copyRole(operator,newRoleName,sourceRoleId);
        return VoHelper.getSuccessResult();
    }

    @Override
    public ResultVO<GetAllFuncPermitRespVO> getAllFuncPermit(String jsonStr) {
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
        String roleId = jsonObject.get("roleId").toString();
        return roleService.getUserByRoleId(operator, roleId);
    }

    @Override
    public ResultVO getRoleUser(String jsonStr) {
        JSONObject jsonObject = StringUtility.parseString(jsonStr);
        String operator = jsonObject.get("operator").toString();
        List<String> roleList = StringUtility.jsonToList(jsonObject.getString("lstRoleId"), String.class);
        return roleService.getRoleUser(operator, roleList);
    }

    @Override
    public ResultVO getMyDataRuleTempl(String jsonStr) {
        JSONObject jsonObject = StringUtility.parseString(jsonStr);
        String operator = jsonObject.get("operator").toString();
        int pageNumber = Integer.valueOf(jsonObject.get("pageNumber").toString());
        int pageData = Integer.valueOf(jsonObject.get("pageData").toString());
        return dataRuleService.getMyDataRuleTempl(pageNumber, pageData, operator);
    }

    @Override
    public List<DataRuleVO> getDataRuleByUser(String jsonStr) {
        JSONObject jsonObject = StringUtility.parseString(jsonStr);
        String operator = jsonObject.get("operator").toString();
        List<String> lstUserName = StringUtility.jsonToList(jsonObject.getString("lstUserName"), String.class);
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
        List<String> lstRoleId = StringUtility.jsonToList(jsonObject.getString("lstRoleId"), String.class);
        return roleService.getRolePermission(operator, lstRoleId);
    }

    @Override
    public ResultVO getUserByUserName(String jsonStr) {
        JSONObject jsonObject = StringUtility.parseString(jsonStr);
        String operator = jsonObject.get("operator").toString();
        UserVO userVO = StringUtility.parseObject(jsonObject.getJSONObject("user").toString(), UserVO.class);
        return organizationService.getUserByUserName(operator, userVO);
    }

    @Override
    @Log(value = "获取用户可选择的所有数据授权方案",level = LogLevel.INFO)
    public ResultVO<List<SysAuthWayVO>> getMyAuthWay(String jsonStr) {
        JSONObject jsonObject = StringUtility.parseString(jsonStr);
        String operator = jsonObject.get("operator").toString();
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
        List<RoleVO> lstRole = StringUtility.jsonToList(jsonObject.getString("lstRole"), RoleVO.class);
        return roleService.updateUsersOfRole(lstRole, operator);
    }

    @Override
    @Log(value = "更新多个角色的功能权限",level = LogLevel.INFO)
    public ResultVO updateRolePermission(String jsonStr) {
        JSONObject jsonObject = StringUtility.parseString(jsonStr);
        String operator = jsonObject.get("operator").toString();
        List<RoleVO> lstRole = StringUtility.parseObject(jsonObject.get("lstRole").toString(), List.class);
        return roleService.updateRolePermission(operator, lstRole);
    }
    @Override
    public ResultVO logout(String jsonStr) {
        return userService.logout(jsonStr);
    }


    @Override
    public ResultVO getMavenPackageTime() {
        return operationBp.getMavenPackageTime();
    }

    /**
     * Description: 新增或编辑一个方案
     *
     * @param :
     * @return:
     * @auther: lvcr
     * @date: 2018/6/20 15:31
     * @see
     */
    @Override
    public ResultVO addOrUpdateDataRuleTempl(String jsonStr) {
        return dataRuleService.addOrUpdateDataRuleTempl(jsonStr);
    }

    /**
     * Description: 删除一个或多个方案
     *
     * @param jsonStr@return:
     * @auther: lvcr
     * @date: 2018/6/20 15:34
     * @see
     */
    @Override
    public ResultVO deleteDataRuleTempl(String jsonStr) {
        return dataRuleService.deleteDataRuleTempl(jsonStr);
    }

    /**
     * Description: 查看用户的功能权限列表
     *
     * @param jsonStr@return:
     * @auther: lvcr
     * @date: 2018/6/20 15:35
     * @see
     */
    @Override
    public ResultVO getUserPermissionList(String jsonStr) {
        return permissionService.getUserPermissionList(jsonStr);
    }

    /**
     * Description: 创建或更新多个用户的数据权限
     *
     * @param jsonStr@return:
     * @auther: lvcr
     * @date: 2018/6/20 15:39
     * @see
     */
    @Override
    public ResultVO addOrUpdateDataRule(String jsonStr) {
        return null;
    }

    /**
     * Description: 多条件搜索角色
     *
     * @param jsonStr@return:
     * @auther: lvcr
     * @date: 2018/6/20 15:41
     * @see
     */
    @Override
    public ResultVO getRolesByInfo(String jsonStr) {
        return roleService.getRolesByInfo(jsonStr);
    }

    /**
     * Description: 新增或更新角色、功能权限、用户
     *
     * @param jsonStr@return:
     * @auther: lvcr
     * @date: 2018/6/20 15:42
     * @see
     */
    @Override
    public ResultVO addOrUpdateRoleInfo(String jsonStr) {
        return roleService.addOrUpdateRoleInfo(jsonStr);
    }

    /**
     * Description: 根据角色id获取角色信息
     *
     * @param jsonStr@return:
     * @auther: lvcr
     * @date: 2018/6/20 15:44
     * @see
     */
    @Override
    public ResultVO getRoleByRoleId(String jsonStr) {
        return roleService.getRoleByRoleId(jsonStr);
    }

    /**
     * Description: 删除多个角色
     *
     * @param jsonStr@return:
     * @auther: lvcr
     * @date: 2018/6/20 15:46
     * @see
     */
    @Override
    public ResultVO deleteRoles(String jsonStr) {
        return roleService.deleteRoles(jsonStr);
    }

}
