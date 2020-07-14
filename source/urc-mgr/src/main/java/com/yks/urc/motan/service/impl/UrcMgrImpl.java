package com.yks.urc.motan.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yks.urc.exception.ErrorCode;
import com.yks.urc.exception.URCBizException;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.fw.constant.StringConstant;
import com.yks.urc.log.Log;
import com.yks.urc.log.LogLevel;
import com.yks.urc.motan.MotanSession;
import com.yks.urc.motan.service.api.IUrcMgr;
import com.yks.urc.sellerid.bp.api.ISellerIdBp;
import com.yks.urc.service.api.IDataRuleService;
import com.yks.urc.service.api.IPermissionService;
import com.yks.urc.service.api.IPersonService;
import com.yks.urc.service.api.IRoleService;
import com.yks.urc.vo.DataRuleSysVO;
import com.yks.urc.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

public class UrcMgrImpl implements IUrcMgr {

    @Autowired
    private IDataRuleService dataRuleService;

    @Autowired
    IPermissionService permissionService;


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
}
