package com.yks.urc.motan.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yks.urc.enums.CommonMessageCodeEnum;
import com.yks.urc.exception.ErrorCode;
import com.yks.urc.exception.URCBizException;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.fw.constant.StringConstant;
import com.yks.urc.log.Log;
import com.yks.urc.log.LogLevel;
import com.yks.urc.motan.MotanSession;
import com.yks.urc.motan.service.api.IUrcMgr;
import com.yks.urc.sellerid.bp.api.ISellerIdBp;
import com.yks.urc.service.api.*;
import com.yks.urc.vo.DataRuleSysVO;
import com.yks.urc.vo.ResultVO;
import com.yks.urc.vo.helper.VoHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

public class UrcMgrImpl implements IUrcMgr {
    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private IDataRuleService dataRuleService;

    @Autowired
    IPermissionService permissionService;
    @Autowired
    IPositionGroupService positionGroupService;


    @Override
    @Log("获取指定系统的数据权限")
    public ResultVO<List<DataRuleSysVO>> getDataRuleGtDt(String json) {
        JSONObject jObj = MotanSession.getRequest().getJSONObjectArg();
        String sysKey = jObj.getString(StringConstant.sysKey);
        Date dt = StringUtility.convertToDate(jObj.getString("dt"), null);
        Integer pageSize = jObj.getInteger("pageSize");
        return dataRuleService.getDataRuleGtDt(sysKey, dt, pageSize);
    }


    @Autowired
    private ISellerIdBp sellerIdBp;

    @Log("匹配正确的销售账号")
    @Override
    public ResultVO checkSellerId(String jsonStr) {
        return sellerIdBp.checkSellerId(jsonStr);
    }

    @Override
    @Log("快速分配数据权限模板给用户")
    public ResultVO assignDataRuleTempl2User(String jsonStr) throws Exception {
        return dataRuleService.assignDataRuleTempl2User(jsonStr);
    }

    @Override
    @Log("推送系统功能及子功能权限")
    public ResultVO importSysPermit(String jsonStr) {
        return permissionService.importSysPermit(jsonStr);
    }


    @Autowired
    private IRoleService roleService;
    @Autowired
    private IPersonService personService;

    @Override
    public ResultVO getRoleUserByRoleId(String json) throws Exception {
        // for 刊登
        // 根据角色id获取人
        return roleService.getRoleUserByRoleId(json);
    }

    /**
     * @param json
     * @Description 根据用户账号信息模糊查询对应用户的详细信息
     * @Author zengzheng
     * @Date 2020/5/21 16:13
     */
    @Override
    @Log("根据用户账号信息模糊查询对应用户的详细信息")
    public ResultVO getUserInfoDetailByUserName(String json) throws Exception {
        JSONObject jsonObject = StringUtility.parseString(json);
        String operator = MotanSession.getRequest().getOperator();
        String userName = jsonObject.getString("userName");
        Integer exact = jsonObject.getInteger("exact");

        Integer pageData = jsonObject.getInteger("pageData");
        Integer pageNum = jsonObject.getInteger("pageNum");
        return personService.fuzzSearchPersonByName4Account(operator, userName,exact,pageData,pageNum);
    }

    @Override
    public ResultVO getDepartment(String json) {
        JSONObject jsonObject = StringUtility.parseString(json);
        String operator = MotanSession.getRequest().getOperator();
        String orgLevel = jsonObject.getString("orgLevel");
        return personService.getDepartment(orgLevel);
    }

    @Log("获取指定系统的平台编码")
    @Override
    public ResultVO getPlatformCode(String jsonStr) {
        return dataRuleService.getPlatformCode(jsonStr);
    }

    @Override
    @Log("获取多个角色已有的功能权限")
    public ResultVO getRolePermission(String jsonStr) {
        JSONObject jsonObject = StringUtility.parseString(jsonStr);
        String operator = jsonObject.getString("operator");

        if (StringUtility.isNullOrEmpty(operator)) {
            throw new URCBizException("operator为空", ErrorCode.E_000002);
        }

        if (StringUtility.isNullOrEmpty(jsonObject.getString("lstRoleId"))) {
            throw new URCBizException("lstRoleId为空", ErrorCode.E_000002);
        }

        List<String> lstRoleId = StringUtility.jsonToList(jsonObject.getString("lstRoleId"), String.class);

        return roleService.getRolePermission(operator, lstRoleId);
    }

    @Override
    @Log("创建或更新多个用户的数据权限")
    public ResultVO addOrUpdateDataRule(String jsonStr) {
        return dataRuleService.addOrUpdateDataRule(jsonStr);
    }


    @Override
    @Log(value = "更新多个角色的功能权限", level = LogLevel.INFO)
    public ResultVO updateRolePermission(String jsonStr) {
        return roleService.updateRolePermission(jsonStr);
    }

    @Autowired
    private IUserService userService;

    @Log("搜索用户上网账号和用户名")
    @Override
    public  ResultVO  searchUserPerson(String jsonStr)  {
        return  userService.searchUserPerson(jsonStr);
    }

    @Autowired
    private IOrganizationService organizationService;

    @Override
    @Log("获取全部用户及组织结构")
    public ResultVO getAllOrgTreeAndUser(){
        return organizationService.getAllOrgTreeAndUserV2();
    }

    @Log("精确匹配批量搜索用户账号")
    @Override
    public ResultVO  searchMatchUserPerson(String jsonStr)  {
        return userService.searchMatchUserPerson(jsonStr);
    }

    @Autowired
    private ISystemService systemService;

    @Override
    @Log("获取系统下拉框列表")
    public ResultVO getSystem(String jsonStr) {
        return systemService.getSystemList();
    }

    @Override
    @Log("获取系统的功能权限")
    public ResultVO getSystemPermission(String jsonStr) {
        return systemService.getSystemPermission(jsonStr);
    }

    @Override
    @Log("系统管理编辑")
    public ResultVO editSystemInfo(String jsonStr) {
        try {
            return systemService.getUpdateSystemInfo(jsonStr);
        } catch (Exception e) {
            logger.error("getUpdateSystemInfo error!", e);
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "系统信息更新失败");
        }
    }

    @Override
    @Log("获取系统信息列表")
    public ResultVO getSystemInfo(String jsonStr) {
        return systemService.getSystemInfoList(jsonStr);
    }


    @Override
    @Log("获取岗位用户列表")
    public ResultVO getUserByPosition(String jsonStr) {
        return userService.getUserByPosition(jsonStr);
    }

    @Override
    @Log("设置为超级管理员")
    public ResultVO setSupperAdmin(String jsonStr) {
        return userService.setSupperAdmin(jsonStr);
    }

    @Override
    @Log("获取当前用户能授权的功能权限-岗位权限调用")
    public ResultVO getUserAuthorizablePermissionForPosition(String jsonStr) {
        return userService.getUserAuthorizablePermissionForPosition(jsonStr);
    }

    @Override
    @Log("保存岗位功能权限")
    public ResultVO savePositionPermission(String jsonStr) {
        return userService.savePositionPermission(jsonStr);
    }

    @Override
    @Log("获取用户的权限组")
    public ResultVO getPermissionGroupByUser(String jsonStr) {
        return positionGroupService.getPermissionGroupByUser(jsonStr);
    }

    @Override
    @Log("删除权限组信息")
    public ResultVO deletePermissionGroup(String jsonStr) {
        return positionGroupService.deletePermissionGroup(jsonStr);
    }

    @Override
    @Log("添加或更新权限组")
    public ResultVO addOrUpdatePermissionGroup(String jsonStr) {
        return positionGroupService.addOrUpdatePermissionGroup(jsonStr);
    }

}
