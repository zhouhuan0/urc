package com.yks.urc.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yks.urc.cache.bp.api.ICacheBp;
import com.yks.urc.constant.UrcConstant;
import com.yks.urc.entity.*;
import com.yks.urc.enums.CommonMessageCodeEnum;
import com.yks.urc.exception.ErrorCode;
import com.yks.urc.exception.URCBizException;
import com.yks.urc.funcjsontree.bp.api.IFuncJsonTreeBp;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.fw.constant.StringConstant;
import com.yks.urc.mapper.*;
import com.yks.urc.operation.bp.api.IOperationBp;
import com.yks.urc.permitStat.bp.api.IPermitRefreshTaskBp;
import com.yks.urc.service.api.IPermissionService;
import com.yks.urc.session.bp.api.ISessionBp;
import com.yks.urc.userValidate.bp.api.IUserValidateBp;
import com.yks.urc.vo.*;
import com.yks.urc.vo.helper.VoHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class PermissionServiceImpl implements IPermissionService {

    private Logger logger = LoggerFactory.getLogger(PermissionServiceImpl.class);

    @Value("${importSysPermit.aesPwd}")
    private String aesPwd;

    @Autowired
    PermissionMapper permissionMapper;

    @Autowired
    private IUserRoleMapper userRoleMapper;

    @Autowired
    IRoleMapper roleMapper;

    @Autowired
    IRolePermissionMapper rolePermissionMapper;

    @Autowired
    private IUserPermitStatMapper userPermitStatMapper;

    @Autowired
    IOperationBp operationBp;

    @Autowired
    ICacheBp cacheBp;

    @Autowired
    private IUserMapper iUserMapper;
    @Autowired
    private IUserValidateBp userValidateBp;
    @Autowired
    private ISessionBp sessionBp;
    @Autowired
    private IFuncJsonTreeBp funcJsonTreeBp;
    @Autowired
    private IRolePermissionMapper rolePermitMapper;
    @Autowired
    private PermitItemPositionMapper permitItemPositionMapper;
    @Autowired
    private PermissionMapper permitMapper;

    @Transactional
    @Override
    public ResultVO importSysPermit(String jsonStr) {
        ResultVO rslt = new ResultVO();
        try {
            JSONObject jsonObject = StringUtility.parseString(jsonStr);
            //???????????????
            String operator = jsonObject.get("operator").toString();
            //????????????
            String pwd = jsonObject.get("pwd").toString();
            //????????????
            String data = jsonObject.get("data").toString();
            if (StringUtility.isNullOrEmpty(operator)) {
                return VoHelper.getErrorResult();
            }
            //????????????????????????
            if (!aesPwd.equals(pwd)) {
                return VoHelper.getErrorResult();
            }
            SystemRootVO[] arr = StringUtility.parseObject(data, new SystemRootVO[0].getClass());
            // ??????key?????????????????????
            List<String> keys = new ArrayList<>();
            if (this.duplicateKey(arr, keys)) {
                if (!CollectionUtils.isEmpty(keys)) {
                    //??????????????????
                    Set<String> duplicateKeys = keys.stream().filter(key -> Collections.frequency(keys, key) > 1).collect(Collectors.toSet());
                    if (!CollectionUtils.isEmpty(duplicateKeys)) {
                        return VoHelper.getErrorResult(CommonMessageCodeEnum.HANDLE_DATA_EXCEPTION.getCode(), String.format("?????????????????????????????????key??????keys =%s", duplicateKeys.toString()));
                    }
                }
            }
            if (arr != null && arr.length > 0) {
                List<PermissionDO> lstPermit = new ArrayList<>(arr.length);
                for (SystemRootVO root : arr) {
                    if (root.apiUrlPrefix == null || root.apiUrlPrefix.size() == 0) {
                        return VoHelper.getErrorResult(CommonMessageCodeEnum.PARAM_NULL.getCode(), "apiUrlPrefix ????????????");
                    }
                    PermissionDO p = new PermissionDO();
                    p.setApiUrlPrefixJson(StringUtility.toJSONString_NoException(root.apiUrlPrefix));
                    p.setSysName(root.system.name);
                    p.setSysKey(root.system.key);
                    p.setSysType(root.system.sysType == null ? UrcConstant.SysType.ERP : root.system.sysType);
                    p.setSysTypeName(root.system.sysTypeName);
                    // ???API ????????????null. ?????????sysContext
                    root.apiUrlPrefix = null;
                    p.setSysContext(StringUtility.toJSONString_NoException(root));
                    p.setCreateTime(new Date());
                    p.setModifiedTime(new Date());
                    p.setCreateBy(sessionBp.getOperator());
                    p.setModifiedBy(sessionBp.getOperator());
                    lstPermit.add(p);
                }
                // ????????????
                for (PermissionDO p : lstPermit) {
                    //???????????????
                    if (p == null) {
                        continue;
                    }
                    PermissionDO pFromDb = permissionMapper.getPermission(p.getSysKey());
                    if (pFromDb == null) {
                        // insert
                        permissionMapper.insertSysPermitDefine(Arrays.asList(p));
                    } else {
                        // update
                        permissionMapper.updateSysContextBySysKey(p);
                    }
                    //????????????
                    cacheBp.insertSysContext(p.getSysKey(), p.getSysContext());
                    //??????API??????
                    this.updateApiPrefixCache();
                   /* //?????????????????????, ?????????????????? ?????????
                    if (updateRolePermissionAndCache(p)){ continue;}*/

                }
                permitRefreshTaskBp.addPermitRefreshTaskForImportSysPermit(Arrays.asList(arr).stream().map(c -> c.system.key).collect(Collectors.toList()));
                operationBp.addLog(PermissionServiceImpl.class.getName(), String.format("??????????????????:%s", data), null);
                rslt.state = CommonMessageCodeEnum.SUCCESS.getCode();
                rslt.msg = "????????????";
            } else {
                rslt.state = CommonMessageCodeEnum.FAIL.getCode();
                rslt.msg = "?????? data";
            }
        } catch (Exception ex) {
            logger.error(String.format("importSysPermit:%s", jsonStr), ex);
            throw new URCBizException(ex.getMessage(), ErrorCode.E_000000);
        }
        //?????????????????????,??????????????????
        saveSuperAdministrator();
        return rslt;
    }

    @Autowired
    private IPermitRefreshTaskBp permitRefreshTaskBp;

    /**
     * ??????key???
     *
     * @param
     * @return
     * @Author lwx
     * @Date 2019/4/27 10:00
     */
    private Boolean duplicateKey(SystemRootVO[] arr, List<String> keys) {
        if (arr == null || arr.length == 0 || keys == null) {
            return true;
        }
        for (int i = 0, size = arr.length; i < size; i++) {
            SystemRootVO systemRootVO = arr[i];
            if (systemRootVO == null || CollectionUtils.isEmpty(systemRootVO.menu)) {
                return true;
            }
            systemRootVO.menu.forEach(menuVO -> {
                if (StringUtils.isNotEmpty(menuVO.key)) {
                    keys.add(menuVO.key);
                }
                if (CollectionUtils.isEmpty(menuVO.module)) {
                    return;
                }
                this.foreachModule(menuVO.module, keys);
            });
        }
        return true;
    }

    private void foreachModule(List<ModuleVO> module, List<String> keys) {
        if (CollectionUtils.isEmpty(module) || keys == null) {
            return;
        }

        module.forEach(moduleVO -> {
            if (StringUtils.isNotEmpty(moduleVO.key)) {
                keys.add(moduleVO.key);
            }
            if (!CollectionUtils.isEmpty(moduleVO.function)) {
                this.foreachModule(moduleVO.module, keys);
            }
            if (!CollectionUtils.isEmpty(moduleVO.function)) {
                this.foreachFunction(moduleVO.function, keys);
            }
        });

    }

    private void foreachFunction(List<FunctionVO> function, List<String> keys) {
        if (CollectionUtils.isEmpty(function) || keys == null) {
            return;
        }
        function.forEach(functionVO -> {
            if (StringUtils.isNotEmpty(functionVO.key)) {
                keys.add(functionVO.key);
            }
            if (!CollectionUtils.isEmpty(functionVO.function)) {
                foreachFunction(functionVO.function, keys);
            }
        });
    }

    /**
     * ??????????????????????????????????????????urc_role_user_affected
     */
    @Override
    public void saveSuperAdministrator() {
        try {
            List<RoleDO> roleDOList = roleMapper.selectAllSuperAdministratorRole();
            List<PermissionDO> lstPermit = permitMapper.getAllSysPermit();
            List<String> lstUserName = new ArrayList<>();
            for (RoleDO roleDO : roleDOList) {
                List<RolePermissionDO> lstRolePermit = new ArrayList<>();
                for (PermissionDO permit : lstPermit) {
                    RolePermissionDO rp = new RolePermissionDO();
                    rp.setRoleId(roleDO.getRoleId());
                    rp.setSysKey(permit.getSysKey());
                    rp.setSelectedContext(permit.getSysContext());
                    rp.setCreateTime(new Date());
                    rp.setCreateBy(sessionBp.getOperator());
                    rp.setModifiedBy(sessionBp.getOperator());
                    rp.setModifiedTime(rp.getCreateTime());
                    lstRolePermit.add(rp);
                }
                rolePermitMapper.deleteByRoleId(roleDO.getRoleId());
                rolePermitMapper.insertBatch(lstRolePermit);

                //???????????????????????????permit_item_position???
                if(StringUtility.stringEqualsIgnoreCaseObj(roleDO.getRoleType(),UrcConstant.RoleType.position)){
                    List<String> list = new ArrayList<>();
                    for (PermissionDO rolePermissionDO : lstPermit) {
                        //?????????????????????
                        list.addAll(funcJsonTreeBp.concatData(rolePermissionDO.getSysContext()));
                    }
                    //???????????????
                    if (!CollectionUtils.isEmpty(list)) {
                        List<PermitItemPosition> param = list.stream().map(e -> {
                            PermitItemPosition vo = new PermitItemPosition();
                            vo.setPermitKey(e);
                            vo.setPositionId(roleDO.getRoleId());
                            vo.setModifier(sessionBp.getOperator());
                            vo.setCreator(sessionBp.getOperator());
                            vo.setCreatedTime(new Date());
                            vo.setModifiedTime(new Date());
                            return vo;
                        }).collect(Collectors.toList());
                        //????????????
                        permitItemPositionMapper.deleteBypositionId(roleDO.getRoleId());
                        permitItemPositionMapper.insertPosition(param);
                    }
                }

                // ???????????????????????????????????????
                UserRoleDO userRoleDO = new UserRoleDO();
                userRoleDO.setRoleId(roleDO.getRoleId());
                lstUserName.addAll(userRoleMapper.getUserNameByRoleId(userRoleDO));
            }
            // ????????????,????????????????????????????????????
            permitRefreshTaskBp.addPermitRefreshTask(lstUserName);

            //Long roleId = roleMapper.selectAllSuperAdministrator();
            //updateAffectedUserPermitCache.assignAllPermit2SuperAdministrator(roleId);
        } catch (Exception e) {
            logger.error("saveSuperAdministrator error! {}", e);
        }
    }

    @Override
    public ResultVO getUserAuthorizablePermission(String userName, String sysType) {
        List<PermissionVO> permissionVOs = new ArrayList<PermissionVO>();
        if (roleMapper.isSuperAdminAccount(userName)) {
            List<PermissionDO> lstSysKey = null;
            //?????????????????????????????????
            if (StringUtils.isEmpty(sysType)) {
                lstSysKey = permissionMapper.getAllSysKey();
            } else {
                lstSysKey = permissionMapper.getSysKey(sysType);
            }
            for (PermissionDO permission : lstSysKey) {
                PermissionVO permissionVO = new PermissionVO();
                permissionVO.setSysKey(permission.getSysKey());
                permissionVO.setSysContext(permission.getSysContext());
                permissionVOs.add(permissionVO);
            }
        } else {
            //?????????????????????????????????????????????
            List<PermissionDO> userAuthorizablePermissionForPosition = null;
            if (StringUtils.isEmpty(sysType)) {
                userAuthorizablePermissionForPosition = iUserMapper.getUserAuthorizablePermissionForPosition(userName);
            } else {
                userAuthorizablePermissionForPosition = iUserMapper.getUserPermission(userName, sysType);
            }
            for (PermissionDO permissionDO : userAuthorizablePermissionForPosition) {
                PermissionVO permissionVO = new PermissionVO();
                permissionVO.setSysKey(permissionDO.getSysKey());
                permissionVO.setSysContext(permissionDO.getSysContext());
                permissionVOs.add(permissionVO);
            }
            List<String> collect = userAuthorizablePermissionForPosition.stream().map(PermissionDO::getSysKey).collect(Collectors.toList());
            //??????????????????????????????
            List<String> lstSysKey = null;
            if (StringUtils.isEmpty(sysType)) {
                lstSysKey = userRoleMapper.getSysKeyByUser(userName);
            } else {
                lstSysKey = userRoleMapper.getSysKeyByUserAndType(userName, sysType);
            }
            //?????????????????????????????????
            lstSysKey.removeAll(collect);
            if (lstSysKey != null && lstSysKey.size() > 0) {
//                lstSysKey.remove("004");
                for (String sysKey : lstSysKey) {
                    // ????????????sys???????????????json (?????????????????????????????????)
                    List<String> lstFuncJson = roleMapper.getBizAdminFuncJsonByUserAndSysKey(userName, sysKey);
                    if (lstFuncJson != null && lstFuncJson.size() > 0) {
                        // ??????json???
                        SystemRootVO rootVO = userValidateBp.mergeFuncJson2Obj(lstFuncJson);
                        if (null == rootVO) continue;
                        // ???????????????????????????????????????
                        if (StringConstant.URC_SYS_KEY.equalsIgnoreCase(sysKey)) {
                            Optional<MenuVO> op = rootVO.menu.stream().filter(c -> StringUtility.stringEqualsIgnoreCase(c.key, StringConstant.URC_PERMIT_KEY)).findFirst();
                            if (op.isPresent()) {
                                rootVO.menu.remove(op.get());
                            }
                        }
                        PermissionVO permissionVO = new PermissionVO();
                        permissionVO.setSysKey(sysKey);
                        permissionVO.setSysContext(StringUtility.toJSONString_NoException(rootVO));
                        permissionVOs.add(permissionVO);
                    }
                }
            }
        }
        return VoHelper.getSuccessResult(permissionVOs);
    }

    /**
     * Description: ??????????????????--?????????????????????????????????????????????????????????
     *
     * @param :
     * @return:
     * @auther: lvcr
     * @date: 2018/6/18 23:34
     * @see
     */
    @Override
    public ResultVO getUserPermissionList(String jsonStr) {
        /*1???json????????????json??????*/
        JSONObject jsonObject = StringUtility.parseString(jsonStr);
        /*2?????????????????????????????????????????????????????????Map*/
        Map<String, Object> queryMap = new HashMap<>();
        checkAndConvertParam(queryMap, jsonObject);
        /*3???????????????????????????????????????*/
        List<UserPermitStatDO> userPermitStatDOS = userPermitStatMapper.listUserPermitStatsByPage(queryMap);
        /*4???List<DO> ??? List<VO>*/
        List<UserPermitStatVO> userPermitStatVOs = convertUserPermitStatDoToVO(userPermitStatDOS);
        /*5??????????????????*/
        Long total = userPermitStatMapper.getCounts(queryMap.get("userName").toString());
        PageResultVO pageResultVO = new PageResultVO(userPermitStatVOs, total, queryMap.get("pageSize").toString());
        return VoHelper.getSuccessResult(pageResultVO);
    }
    /* */

    /**
     * ????????????API??????
     *
     * @return
     * @param:
     * @Author lwx
     * @Date 2018/7/17 15:44
     *//*
    @Override
    public ResultVO updateApiPrefixCache() {
        try {
            List<PermissionDO> permissionDOList =permissionMapper.getSysApiUrlPrefix();
            cacheBp.setSysApiUrlPrefix(permissionDOList);
            return VoHelper.getErrorResult(CommonMessageCodeEnum.SUCCESS.getCode(),"??????API????????????");
        } catch (Exception e) {
            logger.error("????????????",e);
           return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(),"????????????");
        }
    }*/
    private List<UserPermitStatVO> convertUserPermitStatDoToVO(List<UserPermitStatDO> userPermitStatDOS) {
        List<UserPermitStatVO> userPermitStatVOS = new ArrayList<>();
        for (UserPermitStatDO userPermitStatDO : userPermitStatDOS) {
            UserPermitStatVO userPermitStatVO = new UserPermitStatVO();
            BeanUtils.copyProperties(userPermitStatDO, userPermitStatVO);
            userPermitStatVO.setFuncDesc(userPermitStatDO.getFuncJson());
            userPermitStatVO.setSysName(userPermitStatDO.getPermissionDO().getSysName());
            userPermitStatVOS.add(userPermitStatVO);
        }
        return userPermitStatVOS;
    }

    /**
     * Description: ???????????????????????????
     *
     * @param :
     * @return:
     * @auther: lvcr
     * @date: 2018/6/13 11:54
     * @see
     */
    private void checkAndConvertParam(Map<String, Object> queryMap, JSONObject jsonObject) {
        String operator = jsonObject.getString("operator");
        String userName = jsonObject.getString("userName");
        if (StringUtility.isNullOrEmpty(operator) || StringUtility.isNullOrEmpty(userName)) {
            throw new URCBizException("parameter operator or userName is null", ErrorCode.E_000002);
        }
        /*operator=userName??? ??????????????????;operator!=userName??? ??????????????????   ps:?????????????????????????????????????????????*/
        if (!(operator.trim().equals(userName.trim()))) {
            Boolean isAdmin = roleMapper.isAdminOrSuperAdmin(operator);
            if (!isAdmin) {
                throw new URCBizException(String.format("???????????????%s???????????????????????????????????????????????????????????????????????????", operator), ErrorCode.E_000003);
            }
        }
        queryMap.put("userName", userName);
        String pageNumber = jsonObject.getString("pageNumber");
        String pageData = jsonObject.getString("pageData");
        if (!StringUtility.isNum(pageNumber) || !StringUtility.isNum(pageData)) {
            throw new URCBizException("parameter operator is null", ErrorCode.E_000003);
        }
        int currPage = Integer.valueOf(pageNumber);
        int pageSize = Integer.valueOf(pageData);
        queryMap.put("currIndex", (currPage - 1) * pageSize);
        queryMap.put("pageSize", pageSize);
    }

    /**
     * ??????API??????
     *
     * @param
     * @return
     * @Author lwx
     * @Date 2018/8/15 10:21
     */
    @Override
    public ResultVO updateApiPrefixCache() {
        try {
            List<PermissionDO> permissionDOList = permissionMapper.getSysApiUrlPrefix();
            cacheBp.setSysApiUrlPrefix(permissionDOList);
            return VoHelper.getErrorResult(CommonMessageCodeEnum.SUCCESS.getCode(), "??????API????????????");
        } catch (Exception e) {
            logger.error("????????????", e);
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "????????????");
        }
    }

    /**
     * ???????????????????????????
     *
     * @param
     * @return
     * @Author lwx
     * @Date 2018/11/2 10:33
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO deleteSysPermitNode(FuncTreeVO funcTreeVO) {
        try {
            return funcJsonTreeBp.deleteSysPermitNode(funcTreeVO);
        } catch (Exception e) {
            logger.error("??????????????????,????????????:", e);
        }
        return null;
    }


    /**
     * ???????????????????????????
     *
     * @param
     * @return
     * @Author lwx
     * @Date 2018/11/2 15:46
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO updateSysPermitNode(FuncTreeVO funcTreeVO) {
        try {
            return funcJsonTreeBp.updateSysPermitNode(funcTreeVO);
        } catch (Exception e) {
            logger.error("??????????????????,??????????????????", e.getMessage());
        }
        return null;
    }

    @Override
    public List<String> getUserAuthorizableSysKey(String operator) {
        //????????????ERP??????
        ResultVO<List<PermissionVO>> rslt = getUserAuthorizablePermission(operator, "0");
        return rslt.data.stream().map(c -> c.getSysKey()).distinct().collect(Collectors.toList());
    }

    @Override
    public ResultVO getAllPermission(String sysType) {
        List<PermissionVO> permissionVOs = new ArrayList<>();
        List<PermissionDO> lstSysKey = null;
        //?????????????????????????????????
        if (StringUtils.isEmpty(sysType)) {
            lstSysKey = permissionMapper.getAllSysKey();
        } else {
            lstSysKey = permissionMapper.getSysKey(sysType);
        }
        for (PermissionDO permission : lstSysKey) {
            PermissionVO permissionVO = new PermissionVO();
            permissionVO.setSysKey(permission.getSysKey());
            permissionVO.setSysContext(permission.getSysContext());
            permissionVOs.add(permissionVO);
        }
        return VoHelper.getSuccessResult(permissionVOs);
    }

}
