package com.yks.urc.motan.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yks.urc.Enum.ModuleCodeEnum;
import com.yks.urc.config.bp.api.IConfigBp;
import com.yks.urc.entity.UrcLog;
import com.yks.urc.enums.CommonMessageCodeEnum;
import com.yks.urc.exception.ErrorCode;
import com.yks.urc.exception.URCBizException;
import com.yks.urc.fw.DateUtil;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.log.Log;
import com.yks.urc.log.LogLevel;
import com.yks.urc.mapper.IDataRuleTemplMapper;
import com.yks.urc.motan.MotanSession;
import com.yks.urc.motan.service.api.IUrcService;
import com.yks.urc.operation.bp.api.IOperationBp;
import com.yks.urc.permitStat.bp.api.IPermitInverseQueryBp;
import com.yks.urc.permitStat.bp.api.IPermitStatBp;
import com.yks.urc.sellerid.bp.api.ISellerIdBp;
import com.yks.urc.service.api.*;
import com.yks.urc.user.bp.api.IUrcLogBp;
import com.yks.urc.user.bp.api.IUserBp;
import com.yks.urc.userValidate.bp.api.IUserValidateBp;
import com.yks.urc.vo.*;
import com.yks.urc.vo.helper.VoHelper;
import net.bytebuddy.asm.Advice;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.*;

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

    @Autowired
    private MonitorMemoryService memoryService;

    @Autowired
    private ICsService csService;
    @Autowired
    private IUserValidateBp userValidateBp;

    @Override
    @Log(value = "????????????", level = LogLevel.INFO)
    public ResultVO syncUserInfo(String json) {
        String operator = MotanSession.getRequest().getOperator();
        return userService.syncUserInfo(operator);
    }

    @Override
    @Log(value = "??????????????????",level = LogLevel.INFO)
    public ResultVO addCsUserGroup(String json) {
        return csService.addCsUserGroup(json);
    }

    @Override
    @Log(value = "????????????????????????",level = LogLevel.INFO)
    public ResultVO editCsUserGroup(String json) {
        return csService.editCsUserGroup(json);
    }

    @Override
    @Log(value = "??????????????????",level = LogLevel.INFO)
    public ResultVO delCsUserGroup(String json) {
        return csService.delCsUserGroup(json);
    }

    @Override
    @Log(value = "login", level = LogLevel.INFO)
    public ResultVO<LoginRespVO> login(Map<String, String> map) {
        return userService.login(map);
    }

    @Override
    @Log("??????????????????")
    public ResultVO syncDingOrgAndUser() {
        String operator = MotanSession.getRequest().getOperator();
        return personService.SynPersonOrgFromDing(operator);
    }

    @Override
    @Log("??????id????????????????????????")
    public ResultVO getUserByDingOrgId(String params) {

        JSONObject jsonObject = StringUtility.parseString(params);
        String dingOrgId = jsonObject.getString("dingOrgId");
        if (!StringUtility.isNum(dingOrgId)) {
            throw new URCBizException("dingOrgId??????", ErrorCode.E_000002);
        }
        String pageNumber = jsonObject.getString("pageNumber");
        String pageData = jsonObject.getString("pageData");
        return personService.getUserByDingOrgId(dingOrgId, pageNumber, pageData);
    }

    @Override
    @Log("????????????????????????")
    public ResultVO getUserByUserInfo(String params) {
        JSONObject jsonObject = StringUtility.parseString(params);
        PersonVO personVo = jsonObject.getObject("user", PersonVO.class);
        String pageNumber = jsonObject.getString("pageNumber");
        String pageData = jsonObject.getString("pageData");
        return personService.getUserByUserInfo(personVo, pageNumber, pageData);
    }

    @Override
    @Log("?????????????????????")
    public ResultVO getAllOrgTree() {
        return organizationService.getAllOrgTree();
    }

    @Override
    @Log("?????????????????????????????????")
    public ResultVO getAllOrgTreeAndUser(){
        return organizationService.getAllOrgTreeAndUserV2();
    }

    /**
     * ????????????????????????
     *
     * @param params
     * @return
     */
    @Override
    @Log(value = "????????????/????????????", level = LogLevel.INFO)
    public ResultVO<PageResultVO> getUsersByUserInfo(String params) {
        JSONObject jsonObject = StringUtility.parseString(params);
        String operator = MotanSession.getRequest().getOperator();
        String pageNumber = jsonObject.getString("pageNumber");
        String pageData = jsonObject.getString("pageData");
        UserVO userVO = StringUtility.parseObject(jsonObject.getString("user"), UserVO.class);
        if (StringUtility.isNullOrEmpty(operator)) {
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "operator??????");
        }
        return userService.getUsersByUserInfo(operator, userVO, pageNumber, pageData);
    }


    /**
     * Description: ???????????????????????????????????????
     *
     * @param :
     * @return:
     * @auther: lvcr
     * @date: 2018/6/13 13:53
     * @see
     */
    @Override
    @Log("???????????????????????????????????????")
    public ResultVO assignDataRuleTempl2User(String jsonStr) throws Exception {
        return dataRuleService.assignDataRuleTempl2User(jsonStr);
    }

    /**
     * Description: ??????templId????????????????????????
     *
     * @param :jsonStr
     * @return: String
     * @auther: lvcr
     * @date: 2018/6/13 13:55
     * @see
     */
    @Override
    @Log("??????templId????????????????????????")
    public ResultVO<DataRuleTemplVO> getDataRuleTemplByTemplId(String jsonStr) {
        return dataRuleService.getDataRuleTemplByTemplId(jsonStr);
    }

    /**
     * Description: ????????????????????????
     *
     * @param :jsonStr
     * @return: String
     * @auther: lvcr
     * @date: 2018/6/13 13:56
     * @see
     */
    @Override
    @Log("????????????????????????")
    public ResultVO<PageResultVO> getDataRuleTempl(String jsonStr) {
        return dataRuleService.getDataRuleTempl(jsonStr);
    }


    @Override
    @Log(value = "??????????????????", level = LogLevel.INFO)
    public ResultVO<List<OmsPlatformVO>> getPlatformList(String jsonStr) {
        String operator = MotanSession.getRequest().getOperator();
        return userService.getPlatformList(operator);
    }

    @Override
    @Log(value = "????????????????????????????????????", level = LogLevel.INFO)
    public ResultVO<List<OmsShopVO>> getShopList(String jsonStr) {
        JSONObject jsonObject = StringUtility.parseString(jsonStr);
        String operator = MotanSession.getRequest().getOperator();
        String platform = jsonObject.getString("platform");
        if (StringUtility.isNullOrEmpty(platform)) {
            return VoHelper.getErrorResult(CommonMessageCodeEnum.PARAM_NULL.getCode(), "??????????????????");
        }
        return userService.getShopList(operator, platform);
    }

    @Override
    @Log("???????????????")
    public ResultVO<Integer> checkDuplicateRoleName(String jsonStr) {
        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
        String operator = jsonObject.getString("operator");
        String newRoleName = jsonObject.getString("newRoleName");
        String roleId = jsonObject.getString("roleId");
        return roleService.checkDuplicateRoleName(operator, newRoleName, roleId);
    }

    @Override
    @Log("????????????")
    public ResultVO copyRole(String jsonStr) {
        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
        String operator = MotanSession.getRequest().getOperator();
        String newRoleName = jsonObject.getString("newRoleName");
        String sourceRoleId = jsonObject.getString("sourceRoleId");
        return roleService.copyRole(operator, newRoleName, sourceRoleId);
    }

    @Override
    @Log("?????????????????????????????????")
    public ResultVO<GetAllFuncPermitRespVO> getAllFuncPermit(String jsonStr) {
        return userService.getAllFuncPermit(jsonStr);
    }

    @Override
    @Log(value = "funcPermitValidate", level = LogLevel.INFO)
    public ResultVO funcPermitValidate(Map<String, String> map) {
        return userService.funcPermitValidate(map);
    }

    @Override
    @Log("??????roleId ????????????")
    public ResultVO getUserByRoleId(String jsonStr) {
        JSONObject jsonObject = StringUtility.parseString(jsonStr);
        String operator = jsonObject.getString("operator");
        String roleId = jsonObject.getString("roleId");

        if (!StringUtility.isNum(roleId)) {
            throw new URCBizException("roleId??????", ErrorCode.E_000002);
        }
        if (StringUtility.isNullOrEmpty(operator)) {
            throw new URCBizException("operator??????", ErrorCode.E_000002);
        }

        return roleService.getUserByRoleId(operator, roleId);
    }

    @Override
    @Log("??????????????????")
    public ResultVO getRoleUser(String jsonStr) {
        JSONObject jsonObject = StringUtility.parseString(jsonStr);
        String operator = jsonObject.getString("operator");


        if (StringUtility.isNullOrEmpty(operator)) {
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "operator??????");
        }
        if (StringUtility.isNullOrEmpty(jsonObject.getString("lstRoleId"))) {
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "lstRoleId??????");
        }

        List<String> roleList = StringUtility.jsonToList(jsonObject.getString("lstRoleId"), String.class);
        return roleService.getRoleUser(operator, roleList);
    }

    @Override
    @Log("????????????????????????")
    public ResultVO getMyDataRuleTempl(String jsonStr) {
/*        JSONObject jsonObject = StringUtility.parseString(jsonStr);
        String operator = jsonObject.getString("operator");
        if (StringUtility.isNullOrEmpty(operator)) {
            throw new URCBizException("operator??????", ErrorCode.E_000002);
        }

        String pageNumber=jsonObject.getString("pageNumber");
        String pageData=jsonObject.getString("pageData");

        return dataRuleService.getMyDataRuleTempl(pageNumber, pageData, operator);*/
        return dataRuleService.getDataRuleTempl(jsonStr);
    }

    @Override
    @Log("?????????????????????????????????")
    public ResultVO getDataRuleByUser(String jsonStr) {
        JSONObject jsonObject = StringUtility.parseString(jsonStr);
        String operator = jsonObject.getString("operator");

        if (StringUtility.isNullOrEmpty(operator)) {
            throw new URCBizException("operator??????", ErrorCode.E_000002);
        }
        if (StringUtility.isNullOrEmpty(jsonObject.getString("lstUserName"))) {
            throw new URCBizException("lstUserName??????", ErrorCode.E_000002);
        }

        String sysKey = jsonObject.getString("sysKey");

        List<String> lstUserName = StringUtility.jsonToList(jsonObject.getString("lstUserName"), String.class);

        return dataRuleService.getDataRuleByUser(lstUserName, operator, sysKey);
    }

    @Override
    @Log("??????????????????????????????????????????")
    public ResultVO getUserAuthorizablePermission(String jsonStr) {
        JSONObject jsonObject = StringUtility.parseString(jsonStr);
        String operator = jsonObject.getString("operator");
        JSONObject dataObject = jsonObject.getJSONObject("data");
        String sysType = "0";
        if(null != dataObject) {
             sysType = dataObject.getString("sysType");
        }
        return permissionService.getUserAuthorizablePermission(operator,sysType);
    }

    @Override
    @Log("???????????????????????????????????????")
    public ResultVO getRolePermission(String jsonStr) {
        JSONObject jsonObject = StringUtility.parseString(jsonStr);
        String operator = jsonObject.getString("operator");

        if (StringUtility.isNullOrEmpty(operator)) {
            throw new URCBizException("operator??????", ErrorCode.E_000002);
        }

        if (StringUtility.isNullOrEmpty(jsonObject.getString("lstRoleId"))) {
            throw new URCBizException("lstRoleId??????", ErrorCode.E_000002);
        }

        List<String> lstRoleId = StringUtility.jsonToList(jsonObject.getString("lstRoleId"), String.class);
        Integer sysType = jsonObject.getInteger("sysType");
        return roleService.getRolePermission(operator, lstRoleId,sysType);
    }

    @Log(value = "??????????????????", level = LogLevel.INFO)
    @Override
    public ResultVO<List<UserVO>> getUserByUserName(String jsonStr) {
        JSONObject jsonObject = StringUtility.parseString(jsonStr);
        String operator = MotanSession.getRequest().getOperator();
        UserVO userVO = StringUtility.parseObject(jsonObject.getString("user"), UserVO.class);
        if (StringUtility.isNullOrEmpty(operator)) {
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "operator??????");
        }
        return userService.getUserByUserName(operator, userVO);
    }

    @Override
    @Log(value = "????????????????????????????????????", level = LogLevel.INFO)
    public ResultVO<List<SysAuthWayVO>> getMyAuthWay(String jsonStr) {
        String operator = MotanSession.getRequest().getOperator();
        if (StringUtility.isNullOrEmpty(operator)) {
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "operator??????");
        }
        return userService.getMyAuthWay(operator);
    }

    @Override
    @Log("????????????????????????????????????")
    public ResultVO fuzzySearchUsersByUserName(String jsonStr) {
        JSONObject jsonObject = StringUtility.parseString(jsonStr);
        String operator = jsonObject.getString("operator");
        String userName = jsonObject.getString("username");

        if (StringUtility.isNullOrEmpty(operator)) {
            throw new URCBizException("operator??????", ErrorCode.E_000002);
        }

        String pageNumber = jsonObject.getString("pageNumber");
        String pageData = jsonObject.getString("pageData");

        return userService.fuzzySearchUsersByUserName(pageNumber, pageData, userName, operator);
    }


    @Override
    @Log("??????????????????????????? ")
    public ResultVO updateUsersOfRole(String jsonStr) {
        JSONObject jsonObject = StringUtility.parseString(jsonStr);
        String operator = jsonObject.getString("operator");
        List<RoleVO> lstRole = StringUtility.jsonToList(jsonObject.getString("lstRole"), RoleVO.class);
        return roleService.updateUsersOfRole(lstRole, operator);
    }

    @Override
    @Log(value = "?????????????????????????????????", level = LogLevel.INFO)
    public ResultVO updateRolePermission(String jsonStr) {
        return roleService.updateRolePermission(jsonStr);
    }

    @Override
    @Log(value = "logout", level = LogLevel.INFO)
    public ResultVO logout(String jsonStr) {
        return userService.logout(jsonStr);
    }


    @Override
    @Log("??????maven???????????????")
    public ResultVO getMavenPackageTime() {
        return operationBp.getMavenPackageTime();
    }

    /**
     * Description: ???????????????????????????
     *
     * @param :
     * @return:
     * @auther: lvcr
     * @date: 2018/6/20 15:31
     * @see
     */
    @Override
    @Log("???????????????????????????")
    public ResultVO addOrUpdateDataRuleTempl(String jsonStr) {
        return dataRuleService.addOrUpdateDataRuleTempl(jsonStr);
    }

    /**
     * Description: ???????????????????????????
     *
     * @param jsonStr@return:
     * @auther: lvcr
     * @date: 2018/6/20 15:34
     * @see
     */
    @Override
    @Log("????????????")
    public ResultVO deleteDataRuleTempl(String jsonStr) {
        return dataRuleService.deleteDataRuleTempl(jsonStr);
    }

    @Override
    @Log("???????????????")
    public ResultVO<Integer> checkDuplicateTemplName(String jsonStr) {
        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
        String operator = jsonObject.getString("operator");
        String newTemplName = jsonObject.getString("newTemplName");
        String templId = jsonObject.getString("templId");
        return dataRuleService.checkDuplicateTemplName(operator, newTemplName, templId);
    }

    /**
     * Description: ?????????????????????????????????
     *
     * @param jsonStr@return:
     * @auther: lvcr
     * @date: 2018/6/20 15:35
     * @see
     */
    @Override
    @Log("?????????????????????????????????")
    public ResultVO getUserPermissionList(String jsonStr) {
        return permissionService.getUserPermissionList(jsonStr);
    }

    /**
     * Description: ??????????????????????????????????????????
     *
     * @param jsonStr@return:
     * @auther: lvcr
     * @date: 2018/6/20 15:39
     * @see
     */
    @Override
    @Log("??????????????????????????????????????????")
    public ResultVO addOrUpdateDataRule(String jsonStr) {
        return dataRuleService.addOrUpdateDataRule(jsonStr);
    }

    /**
     * Description: ?????????????????????
     *
     * @param jsonStr@return:
     * @auther: lvcr
     * @date: 2018/6/20 15:41
     * @see
     */
    @Override
    @Log("?????????????????????")
    public ResultVO getRolesByInfo(String jsonStr) {
        return roleService.getRolesByInfo(jsonStr);
    }

    /**
     * Description: ?????????????????????????????????????????????
     *
     * @param jsonStr@return:
     * @auther: lvcr
     * @date: 2018/6/20 15:42
     * @see
     */
    @Override
    @Log("?????????????????????????????????????????????")
    public ResultVO addOrUpdateRoleInfo(String jsonStr) {
        return roleService.addOrUpdateRoleInfo(jsonStr);
    }

    /**
     * Description: ????????????id??????????????????
     *
     * @param jsonStr@return:
     * @auther: lvcr
     * @date: 2018/6/20 15:44
     * @see
     */
    @Override
    @Log(" ????????????id??????????????????")
    public ResultVO getRoleByRoleId(String jsonStr) {
        return roleService.getRoleByRoleId(jsonStr);
    }

    /**
     * Description: ??????????????????
     *
     * @param jsonStr@return:
     * @auther: lvcr
     * @date: 2018/6/20 15:46
     * @see
     */
    @Override
    @Log("??????????????????")
    public ResultVO deleteRoles(String jsonStr) {
        return roleService.deleteRoles(jsonStr);
    }

    @Override
    @Log("??????roleId ??????????????????????????????")
    public ResultVO assignAllPermit2Role(String jsonStr) {
        return roleService.assignAllPermit2Role();
    }

    @Override
    @Log("??????????????????")
    public ResultVO startMonitorMemory(String jsonStr) {
        return memoryService.startMonitor();
    }

    @Override
    @Log("??????????????????")
    public ResultVO endMonitorMemory(String jsonStr) {
        return memoryService.endMonitor();
    }

    @Override
    @Log("??????????????????")
    public ResultVO handleExpiredRole(String jsonStr) {
        return roleService.handleExpiredRole();
    }

    @Autowired
    private IPermitStatBp permitStatBp;

    @Override
    @Log("??????????????????")
    public ResultVO updateUserPermitCache(String jsonStr) {
        JSONObject jsonObject = StringUtility.parseString(jsonStr);
        List<String> lstUser = StringUtility.parseObject(jsonObject.getString("lstUser"), List.class);
        permitStatBp.updateUserPermitCache(lstUser);
        return VoHelper.getSuccessResult();
    }

    @Override
    @Log("??????????????????????????????????????????")
    public ResultVO operIsSuperAdmin(String jsonStr) {
        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
        String operator = jsonObject.getString("operator");
        if (StringUtility.isNullOrEmpty(operator)) {
            throw new URCBizException("operator??????", ErrorCode.E_000002);
        }
        return roleService.operIsSuperAdmin(operator);

    }

    @Override
    @Log("??????????????????????????????")
    public ResultVO getPlatformShopSite(String jsonStr) {
        String operator = MotanSession.getRequest().getOperator();
        return userService.getPlatformShopSite(operator);

    }

    @Override
    @Log("??????????????????")
    public ResultVO syncPlatform(String jsonStr) {
        String operator = MotanSession.getRequest().getOperator();
        return userService.syncPlatform(operator);
    }

    @Override
    @Log("????????????????????????")
    public ResultVO syncShopSite(String jsonStr) {
        String operator = MotanSession.getRequest().getOperator();
        return userService.syncShopSite(operator);


    }

    @Override
    @Log("??????SSO????????????????????????????????????????????????????????????????????????")
    public ResultVO fuzzSearchPersonByName(String jsonStr) {
        JSONObject jsonObject = StringUtility.parseString(jsonStr);
        String operator = MotanSession.getRequest().getOperator();
        String userName = jsonObject.getString("name");
        return personService.fuzzSearchPersonByName(operator, userName);

    }

    /**
     * ????????????Api??????
     *
     * @return
     * @param:
     * @Author lwx
     * @Date 2018/7/17 15:38
     */
    @Override
    @Log("????????????Api??????")
    public ResultVO updateApiPrefixCache(String json) {
        String operator = MotanSession.getRequest().getOperator();
        return permissionService.updateApiPrefixCache();
    }

    @Override
    @Log("????????????-??????????????????")
    public ResultVO<List<OmsPlatformVO>> getPlatformShop(String json) {
        JSONObject jsonObject = StringUtility.parseString(json);
        String operator = MotanSession.getRequest().getOperator();
        String platformId = jsonObject.getString("platformId");
        //return dataRuleService.getPlatformShop(operator,platformId);
        return null;
    }





    @Override
    @Log("????????????-??????????????????")
    public ResultVO resetPwdSubmit(String jsonStr) {
        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
        String mobile = String.valueOf(jsonObject.get("mobile"));
        String new_password = String.valueOf(jsonObject.get("newPwd"));
        String username = String.valueOf(jsonObject.get("userName"));
        String code = String.valueOf(jsonObject.get("verificationCode"));
        if (StringUtility.isNullOrEmpty(username)) {
            return VoHelper.getResultVO(CommonMessageCodeEnum.PARAM_NULL.getCode(), "????????????????????????");
        }
        if (StringUtility.isNullOrEmpty(new_password)) {
            return VoHelper.getResultVO(CommonMessageCodeEnum.PARAM_NULL.getCode(), "????????????????????????");
        }
        if (StringUtility.isNullOrEmpty(code)) {
            return VoHelper.getResultVO(CommonMessageCodeEnum.PARAM_NULL.getCode(), "????????????????????????");
        }
        return userService.resetPwdSubmit(mobile, new_password, username, code);
    }

    @Override
    @Log("???????????????")
    public ResultVO resetPwdGetVerificationCode(String jsonStr) {
        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
        String username = jsonObject.getString("userName");
        String mobile = jsonObject.getString("mobile");
        if (StringUtility.isNullOrEmpty(username)) {
            return VoHelper.getResultVO(CommonMessageCodeEnum.PARAM_NULL.getCode(), "?????????????????????");
        }
        if (StringUtility.isNullOrEmpty(mobile)) {
            return VoHelper.getResultVO(CommonMessageCodeEnum.PARAM_NULL.getCode(), "?????????????????????");
        }
        return userService.resetPwdGetVerificationCode(username, mobile);
    }


    @Override
    @Log("????????????-????????????????????????entityCode")
    public ResultVO<List<OmsPlatformVO>> getPlatformShopByEntityCode(String json) {
        JSONObject jsonObject = StringUtility.parseString(json);
        String operator = MotanSession.getRequest().getOperator();
        String entityCode = jsonObject.getString("entityCode");
        return dataRuleService.getPlatformShopByEntityCode(operator, entityCode);
    }


    @Override
    @Log("????????????????????????????????????????????????")
    public ResultVO<List<OmsPlatformVO>> appointPlatformShopSite(String json) {
        JSONObject jsonObject = StringUtility.parseString(json);
        String operator = MotanSession.getRequest().getOperator();
        String platformId = jsonObject.getString("platformId");
        return dataRuleService.appointPlatformShopSite(operator, platformId);
    }

    @Log("???????????????????????????")
    @Override
    public ResultVO deleteSysPermitNode(String jsonStr) {
        JSONObject jsonObject =StringUtility.parseString(jsonStr);
        FuncTreeVO funcTreeVO =StringUtility.parseObject(jsonObject.getJSONObject("data").toString(),FuncTreeVO.class);
        if (funcTreeVO == null){
            return VoHelper.getErrorResult(CommonMessageCodeEnum.PARAM_NULL.getCode(),CommonMessageCodeEnum.PARAM_NULL.getDesc());
        }
        return permissionService.deleteSysPermitNode(funcTreeVO);
    }

    @Log("????????????????????????")
    @Override
    public ResultVO updateSysPermitNode(String jsonStr) {
        JSONObject jsonObject =StringUtility.parseString(jsonStr);
        FuncTreeVO funcTreeVO =StringUtility.parseObject(jsonObject.getJSONObject("data").toString(),FuncTreeVO.class);
        if (funcTreeVO == null){
            return VoHelper.getErrorResult(CommonMessageCodeEnum.PARAM_NULL.getCode(),CommonMessageCodeEnum.PARAM_NULL.getDesc());
        }
        return permissionService.updateSysPermitNode(funcTreeVO);
    }
    @Log("??????sku??????,??????")
    @Override
  public   ResultVO getBasicDataList(String jsonStr){
     return  userService.getBasicDataList(jsonStr);
    }
    @Log("????????????????????????")
    @Override
   public  ResultVO  getWarehouse(String jsonStr)  {
        return  userService.getWarehouse(jsonStr);
    }
    @Log("????????????????????????????????????")
    @Override
    public  ResultVO  searchUserPerson(String jsonStr)  {
        return  userService.searchUserPerson(jsonStr);
    }
    @Log("???????????????????????????")
    @Override
    public ResultVO addUrcWhiteApi(String json) {
        try {
            return userValidateBp.addUrcWhiteApi(json);
        } catch (Exception e) {
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(),"?????????????????????????????????.");
        }
    }
    @Log("?????????????????????????????????")
    @Override
    public ResultVO deleteWhiteApi(String json){
      try{
          return userValidateBp.deleteWhiteApi(json);
      }
      catch (Exception e){
          return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(),"?????????????????????????????????");
      }
    }

    @Log("?????????????????????????????????")
	@Override
	public ResultVO searchSellerId(String jsonStr) {
    	try {
    		JSONObject jsonObject = StringUtility.parseString(jsonStr);
        	
        	if(null == jsonObject){
        		return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "????????????");
        	}
        	String entityCode = jsonObject.getString("entityCode");
        	String platformCode = jsonObject.getString("platformCode");
        	if (StringUtility.isNullOrEmpty(entityCode)) {
                return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "entityCode??????");
            }
            if (StringUtility.isNullOrEmpty(platformCode)) {
                return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "platformCode??????");
            }
            String operator = MotanSession.getRequest().getOperator();
            jsonObject.put("operator", operator);
            
            ResultVO resultVOTemp =  dataRuleService.getPlatformShopByConditions(jsonObject);
            List<OmsPlatformVO> omsPlatformVOList = (List<OmsPlatformVO>) resultVOTemp.data;
            SearchSellerIdRespVO searchSellerIdRespVO = new SearchSellerIdRespVO();
            List<String> list = new ArrayList<>();
            if(!CollectionUtils.isEmpty(omsPlatformVOList)){
            	List<OmsShopVO> lstShop = omsPlatformVOList.get(0).lstShop;
                for (OmsShopVO omsShopVO : lstShop) {
                	list.add(omsShopVO.shopId);
                }
            }
            searchSellerIdRespVO.setList(list);
            return VoHelper.getSuccessResult(searchSellerIdRespVO);
		} catch (Exception e) {
			return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(),"???????????????????????????????????????.");
		}
    	
	}

	@Autowired
    private ISellerIdBp sellerIdBp;

    @Log("???????????????????????????")
	@Override
    public ResultVO checkSellerId(String jsonStr) {
        return sellerIdBp.checkSellerId(jsonStr);
    }

    @Log("?????????????????????????????????")
	@Override
	public ResultVO getPlatformCode(String jsonStr) {
        return dataRuleService.getPlatformCode(jsonStr);
	}

	@Override
	public ResultVO getLogModuleList(String jsonStr) {
		try {
			List<LogModuleVo> list = new ArrayList<>();
			LogModuleVo logModuleVoAll = new LogModuleVo();
			logModuleVoAll.setKey("");
			logModuleVoAll.setLabel("??????");
			list.add(logModuleVoAll);
			for(ModuleCodeEnum orderStatusEnum:ModuleCodeEnum.values()){
				LogModuleVo logModuleVo = new LogModuleVo();
				logModuleVo.setKey(orderStatusEnum.getStatus().toString());
				logModuleVo.setLabel(orderStatusEnum.getStatusName());
				list.add(logModuleVo);
	        }
			return VoHelper.getSuccessResult(list);
		} catch (Exception e) {
			return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(),"????????????????????????.");
		}
	}

	@Autowired
	IUrcLogBp iUrcLogBp;
	@Override
	public ResultVO getLogList(String jsonStr) {
		try {
			JSONObject jsonObject = StringUtility.parseString(jsonStr);
	    	
	    	if(null == jsonObject){
	    		return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "????????????");
	    	}
	    	LogListReqVo logListReqVo = StringUtility.parseObject(jsonObject.getString("data"), LogListReqVo.class);
	    	if (null == logListReqVo) {
	            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "data??????");
	        }
	    	if(!CollectionUtils.isEmpty(logListReqVo.getOperateTimeRange()) && logListReqVo.getOperateTimeRange().size() == 2){
	    		logListReqVo.setOperateTimeStart(new Date(logListReqVo.getOperateTimeRange().get(0)));
	    		logListReqVo.setOperateTimeEnd(new Date(logListReqVo.getOperateTimeRange().get(1)));
	    	}
	    	if(logListReqVo.getPageData() == null || logListReqVo.getPageNumber() == null){
	    		logListReqVo.setPageData(20);
	    		logListReqVo.setPageNumber(1);
	    	}
	    	if(!StringUtils.isEmpty(logListReqVo.getModuleCode())){
	    		logListReqVo.setModuleCode4Select(Integer.valueOf(logListReqVo.getModuleCode()));
	    	}
	    	logListReqVo.setOffset(logListReqVo.getPageData() * (logListReqVo.getPageNumber() - 1) );
	    	LogListRespVo logListRespVo = new LogListRespVo();
	    	logListRespVo.setPageSize(logListReqVo.getPageData());
	    	
	    	List<UrcLog> urcLogList = iUrcLogBp.selectUrcLogByConditions(logListReqVo);
	    	logListReqVo.setPageData(null);
	    	logListReqVo.setPageNumber(null);
	    	List<UrcLog> urcLogList4Count = iUrcLogBp.selectUrcLogByConditions(logListReqVo);
	    	List<UrcLogVO> urcLogVOList = new ArrayList<>(urcLogList.size());
	    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    	if(!CollectionUtils.isEmpty(urcLogList)){
	    		for (UrcLog urcLog : urcLogList) {
	    			UrcLogVO urcLogVO = new UrcLogVO();
                    BeanUtils.copyProperties(urcLog, urcLogVO);
					urcLogVO.setOperateTime(urcLog.getOperateTime() != null ? DateUtil.formatDate(urcLog.getOperateTime(), "yyyy-MM-dd HH:mm:ss"): null);
					urcLogVO.setModuleName(ModuleCodeEnum.getOrderState(urcLog.getModuleCode()));
					urcLogVOList.add(urcLogVO);
				}
	    	}
	    	logListRespVo.setList(urcLogVOList);
	    	logListRespVo.setTotal(null != urcLogList4Count?urcLogList4Count.size():0);
	    	logListRespVo.setTotalPage(logListRespVo.getTotal()/logListRespVo.getPageSize()+1);
	    	return VoHelper.getSuccessResult(logListRespVo);
		} catch (Exception e) {
			return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(),"????????????????????????.");
		}
	}
	
	@Autowired
    IUserBp userBp;
	@Override
	public ResultVO getUserName(String jsonStr) {
		try {
			JSONObject jsonObject = StringUtility.parseString(jsonStr);
	    	
	    	if(null == jsonObject){
	    		return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "????????????");
	    	}
	    	UserVO userVO = StringUtility.parseObject(jsonObject.getString("data"), UserVO.class);
	    	if (null == userVO) {
	            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "data??????");
	        }
	    	List<GetUserNameRespVo> userNameList4Return = new ArrayList<>();
	    	GetUserNameRespVo getUserNameRespVoAll = new GetUserNameRespVo();
	    	getUserNameRespVoAll.setUserName("??????");
	    	userNameList4Return.add(getUserNameRespVoAll);
	    	List<String> userNameList = userBp.getUserName(userVO.userName);
	    	List<GetUserNameRespVo> getUserNameRespVoList = new ArrayList<>();
	    	if(!CollectionUtils.isEmpty(userNameList)){
	    		for (String userName : userNameList) {
	    			GetUserNameRespVo getUserNameRespVo = new GetUserNameRespVo();
		    		getUserNameRespVo.setUserName(userName);
		    		getUserNameRespVoList.add(getUserNameRespVo);
				}
	    		
	    	}
	    	userNameList4Return.addAll(getUserNameRespVoList);
	    	return VoHelper.getSuccessResult(userNameList4Return);
		} catch (Exception e) {
			return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(),"?????????????????????.");
		}
	}

    @Override
    public ResultVO getUserListByPermitKey(String json) {
        return permitInverseQueryBp.getUserListByPermitKey(json);
    }

    @Autowired
    private IPermitInverseQueryBp permitInverseQueryBp;

    @Override
    public ResultVO exportUserListByPermitKey(String json) {
        return permitInverseQueryBp.exportUserListByPermitKey(json);
    }

    @Autowired
    private IConfigBp configBp;

    @Override
    public ResultVO updateConfig(String json) {
        return configBp.updateConfig(json);
    }
}
