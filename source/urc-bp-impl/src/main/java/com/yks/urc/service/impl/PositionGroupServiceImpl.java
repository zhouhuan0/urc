package com.yks.urc.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.yks.urc.Enum.ModuleCodeEnum;
import com.yks.urc.config.bp.api.IConfigBp;
import com.yks.urc.constant.UrcConstant;
import com.yks.urc.entity.*;
import com.yks.urc.enums.CommonMessageCodeEnum;
import com.yks.urc.excel.FileUpDownLoadUtils;
import com.yks.urc.excel.PositionInfoExcelExport;
import com.yks.urc.excel.PositionPowerExcelExport;
import com.yks.urc.exception.ErrorCode;
import com.yks.urc.exception.URCBizException;
import com.yks.urc.funcjsontree.bp.api.IFuncJsonTreeBp;
import com.yks.urc.fw.StringUtil;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.mapper.*;
import com.yks.urc.permitStat.bp.api.IPermitRefreshTaskBp;
import com.yks.urc.seq.bp.api.ISeqBp;
import com.yks.urc.serialize.bp.api.ISerializeBp;
import com.yks.urc.service.api.IPositionGroupService;
import com.yks.urc.session.bp.api.ISessionBp;
import com.yks.urc.user.bp.api.IUrcLogBp;
import com.yks.urc.vo.*;
import com.yks.urc.vo.helper.VoHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class PositionGroupServiceImpl implements IPositionGroupService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    IPositionGroupMapper positionGroupMapper;
    @Autowired
    UrcPositionGroupMapper urcPositionGroupMapper;
    @Autowired
    UrcGroupPermissionMapper urcGroupPermissionMapper;
    @Autowired
    private ISeqBp seqBp;
    @Autowired
    private PositionInfoExcelExport positionInfoExcelExport;
    @Autowired
    private ISerializeBp serializeBp;
    @Autowired
    private IRolePermissionMapper rolePermitMapper;
    @Autowired
    private IRolePermissionMapper rolePermissionMapper;
    @Autowired
    PermitItemPositionMapper permitItemPositionMapper;
    @Autowired
    private ISessionBp sessionBp;
    @Autowired
    private IUserRoleMapper userRoleMapper;
    @Autowired
    private IPermitRefreshTaskBp permitRefreshTaskBp;
    @Autowired
    private UrcSystemAdministratorMapper urcSystemAdministratorMapper;
    @Autowired
    private IUrcLogBp iUrcLogBp;
    @Autowired
    private IRoleMapper roleMapper;
    @Autowired
    private IFuncJsonTreeBp funcJsonTreeBp;
    @Autowired
    private RoleOwnerMapper ownerMapper;
    @Autowired
    private PositionPowerExcelExport positionPowerExcelExport;
    @Autowired
    private IConfigBp configBp;
    //????????????????????????(1???????????????0??????)
    private final static String POSITION_EXPORT_FLAG = "POSITION_EXPORT_FLAG";

    private static String excelTemp = "/opt/tmp/";

    @Override
    public ResultVO getPermissionGroupByUser(String jsonStr, String operator) {
        try {
            /* 1??????json???????????????Json?????? */
            JSONObject jsonObject = StringUtility.parseString(jsonStr).getJSONObject("data");
            //???????????????
            String groupName = jsonObject.getString("groupName");
            //?????????
            if (StringUtil.isEmpty(operator)) {
                throw new URCBizException("operator is empty", ErrorCode.E_000003);
            }
            /*??????????????????queryMap*/
            Map<String, Object> queryMap = new HashMap<>();
            int pageNumber = jsonObject.getInteger("pageNumber");
            int pageData = jsonObject.getInteger("pageData");
            if (!StringUtil.isNum(pageNumber) || !StringUtil.isNum(pageData)) {
                throw new URCBizException("pageNumber or  pageData is not a num", ErrorCode.E_000003);
            }
            int currPage = pageNumber;
            int pageSize = pageData;
            queryMap.put("currIndex", (currPage - 1) * pageSize);
            queryMap.put("pageSize", pageSize);
            queryMap.put("groupName", groupName);
            queryMap.put("userName", operator);
            //????????????
            List<PositionGroupVO> list = positionGroupMapper.getPermissionGroupByUser(queryMap);
            //????????????
            int total = positionGroupMapper.getPermissionGroupByUserCount(queryMap);
            PageResultVO pageResultVO = new PageResultVO(list, total, queryMap.get("pageSize").toString());
            return VoHelper.getSuccessResult(pageResultVO);
        } catch (Exception e) {
            logger.error("getPermissionGroupByUser error!", e);
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "??????????????????????????????");
        }

    }

    @Override
    public ResultVO deletePermissionGroup(String jsonStr) {
        try {
            /* 1??????json???????????????Json?????? */
            JSONObject jsonObject = StringUtility.parseString(jsonStr).getJSONObject("data");
            //?????????id
            String groupId = jsonObject.getString("groupId");
            if (StringUtil.isEmpty(groupId)) {
                throw new URCBizException("groupId????????????", ErrorCode.E_000003);
            }
            //????????????
            int num = positionGroupMapper.deletePermissionGroup(groupId);
            return VoHelper.getSuccessResult();
        } catch (Exception e) {
            logger.error("deletePermissionGroup error!", e);
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "??????groupId?????????????????????");
        }
    }

    @Override
    public ResultVO addOrUpdatePermissionGroup(String jsonStr, String operator) {
        try {
            /* 1??????json???????????????Json?????? */
            JSONObject jsonObject = StringUtility.parseString(jsonStr).getJSONObject("data");
            Integer sysType = jsonObject.getInteger("sysType");
            //?????????id
            String groupId = jsonObject.getString("groupId");
            String groupName = jsonObject.getString("groupName");
            String positionIdStr = jsonObject.getString("positionIds");
            List<Long> positionIds = JSONArray.parseArray(positionIdStr, Long.class);
            String selectedContext = jsonObject.getString("selectedContext");
            List<PermissionDO> permissionDOList = serializeBp.json2ObjNew(jsonObject.getString("selectedContext"), new TypeReference<List<PermissionDO>>() {
            });
            List<Map> selectedContextmap = JSONArray.parseArray(selectedContext, Map.class);
            List<String> roleSysKey = new ArrayList<>();
            //?????????????????????????????????
            boolean isSuperAdmin = roleMapper.isSuperAdminAccount(operator);
            if (!isSuperAdmin) {
                String roleId = configBp.getString("special_position");
                if(!StringUtility.isNullOrEmpty(roleId) && userRoleMapper.existsUserName(roleId,operator)){
                    roleSysKey = rolePermissionMapper.getPositionPermission(roleId,sysType == null ? null : sysType.toString()).stream().map(PermissionDO :: getSysKey).collect(Collectors.toList());
                }else{
                    roleSysKey = urcSystemAdministratorMapper.selectSysKeyByAdministratorType(operator, UrcConstant.AdministratorType.functionAdministrator.intValue(), sysType);
                }

                if (CollectionUtils.isEmpty(roleSysKey)) {
                    return VoHelper.getFail("????????????????????????????????????????????????");
                }
            } else {
                //????????????????????????????????????
                roleSysKey = permitItemPositionMapper.findOneSystemKey(sysType);
            }

            //???????????????????????????????????????
            boolean existSuperAdmin = true;
            if (!CollectionUtils.isEmpty(positionIds)) {
                existSuperAdmin = positionGroupMapper.existSuperAdmin(positionIds);
            } else {
                //???????????????????????????????????????
                existSuperAdmin = false;
            }
            if (!existSuperAdmin) {
                Long newGroupId = null;
                if (StringUtils.isEmpty(groupId)) {
                    //???????????????????????????
                    boolean isExist = urcPositionGroupMapper.existName(groupName);
                    if (isExist) {
                        return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "?????????????????????");
                    }
                    newGroupId = seqBp.getNextRoleId();
                } else {
                    newGroupId = Long.parseLong(groupId);
                    //???????????????
                    urcPositionGroupMapper.deleteByGroupId(newGroupId);
                    //??????????????????
                    //urcGroupPermissionMapper.deleteByGroupId(newGroupId);
                    urcGroupPermissionMapper.deleteByGroupIdandKey(newGroupId, roleSysKey);
                }
                List<String> positionList = new ArrayList<String>();
                if (positionIds != null && positionIds.size() > 0) {
                    for (Long positionId : positionIds) {
                        //?????????????????????
                        UrcPositionGroup positionGroup = new UrcPositionGroup();
                        positionGroup.setGroupId(newGroupId);
                        positionGroup.setPositionId(positionId);
                        positionGroup.setGroupName(groupName);
                        positionGroup.setIsDelete((byte) 0);
                        positionGroup.setCreator(operator);
                        positionGroup.setModifier(operator);
                        positionGroup.setModifiedTime(new Date());
                        positionGroup.setCreateTime(new Date());
                        urcPositionGroupMapper.insert(positionGroup);
                        positionList.add(positionId + "");
                    }
                } else {
                    //??????????????????????????????
                    UrcPositionGroup positionGroup = new UrcPositionGroup();
                    positionGroup.setGroupId(newGroupId);
                    positionGroup.setPositionId(null);
                    positionGroup.setGroupName(groupName);
                    positionGroup.setIsDelete((byte) 0);
                    positionGroup.setCreator(operator);
                    positionGroup.setModifier(operator);
                    positionGroup.setModifiedTime(new Date());
                    positionGroup.setCreateTime(new Date());
                    urcPositionGroupMapper.insert(positionGroup);
                }
                //List<String> syskeyList = new ArrayList<String>();
                if (selectedContextmap != null && selectedContextmap.size() > 0) {
                    for (Map map : selectedContextmap) {
                        //?????????????????????
                        UrcGroupPermission groupPermission = new UrcGroupPermission();
                        groupPermission.setGroupId(newGroupId);
                        groupPermission.setSysKey(map.get("sysKey").toString());
                        groupPermission.setSelectedContext(map.get("sysContext").toString());
                        groupPermission.setCreator(operator);
                        groupPermission.setModifier(operator);
                        groupPermission.setModifiedTime(new Date());
                        groupPermission.setCreateTime(new Date());
                        urcGroupPermissionMapper.insert(groupPermission);
                        //syskeyList.add(map.get("sysKey").toString());
                    }
                }
                //??????????????????
                UrcLog urcLog = new UrcLog(sessionBp.getOperator(), ModuleCodeEnum.ROLE_MANAGERMENT.getStatus(), "????????????????????????", groupName, jsonStr);
                iUrcLogBp.insertUrcLog(urcLog);
                //????????????
                //?????????????????????,?????????
                List<RolePermissionDO> lstRolePermit = new ArrayList<>();

                for (String positionId : positionList) {
                    rolePermitMapper.deleteByRoleIdInSysKey(positionId, roleSysKey);
                    if (!CollectionUtils.isEmpty(permissionDOList)) {
                        for (PermissionDO permissionDO : permissionDOList) {
                            RolePermissionDO rp = new RolePermissionDO();
                            rp.setRoleId(Long.parseLong(positionId));
                            rp.setSysKey(permissionDO.getSysKey());
                            rp.setSelectedContext(permissionDO.getSysContext());
                            rp.setCreateTime(new Date());
                            rp.setCreateBy(sessionBp.getOperator());
                            rp.setModifiedBy(sessionBp.getOperator());
                            rp.setModifiedTime(rp.getCreateTime());
                            lstRolePermit.add(rp);
                        }
                        if (!CollectionUtils.isEmpty(lstRolePermit)) {
                            rolePermitMapper.insertBatch(lstRolePermit);
                            //????????????
                            lstRolePermit.clear();
                        }
                    }
                    RolePermissionDO permissionDO = new RolePermissionDO();
                    permissionDO.setRoleId(Long.parseLong(positionId));
                    permissionDO.setSysType(sysType);
                    permissionDO.setSysKeys(roleSysKey);
                    // List<RolePermissionDO> rolePermissionList = rolePermissionMapper.getRoleSuperAdminPermission(permissionDO);
                    List<RolePermissionDO> rolePermissionList = rolePermissionMapper.getRoleSuperAdminPermissionBySysType(permissionDO);
                    List<String> list = new ArrayList<>();
                    for (RolePermissionDO rolePermissionDO : rolePermissionList) {
                        //?????????????????????
                        list.addAll(funcJsonTreeBp.concatData(rolePermissionDO.getSelectedContext()));
                    }
                    //???????????????
                    if (!CollectionUtils.isEmpty(list)) {
                        List<PermitItemPosition> param = list.stream().map(e -> {
                            PermitItemPosition vo = new PermitItemPosition();
                            vo.setPermitKey(e);
                            vo.setPositionId(Long.parseLong(positionId));
                            vo.setModifier(sessionBp.getOperator());
                            vo.setCreator(sessionBp.getOperator());
                            vo.setCreatedTime(new Date());
                            vo.setModifiedTime(new Date());
                            return vo;
                        }).collect(Collectors.toList());
                        //sys_key ????????? permit_key
                        List<String> permitKeys = changeKey(roleSysKey);
                        //????????????
                        permitItemPositionMapper.deleteBypositionIdAndKey(Long.parseLong(positionId), permitKeys);
                        permitItemPositionMapper.insertPosition(param);
                    }

                    //??????????????????????????????userName
                    UserRoleDO userRoleDO = new UserRoleDO();
                    userRoleDO.setRoleId(Long.parseLong(positionId));
                    List<String> oldRelationUsers = userRoleMapper.getUserNameByRoleId(userRoleDO);
                    //??????????????????????????????
                    List<String> lstUserName = new ArrayList<>();
                    //???????????????????????????????????????
                    if (oldRelationUsers != null && !oldRelationUsers.isEmpty()) {
                        lstUserName.addAll(oldRelationUsers);
                    }
                    if (!lstUserName.isEmpty()) {
                        /*??????*/
                        lstUserName = removeDuplicate(lstUserName);
                        //???????????????????????????
                        // ???????????????????????????
                        permitRefreshTaskBp.addPermitRefreshTask(lstUserName);
                    }
                }
                return VoHelper.getSuccessResult();
            }
            return VoHelper.getSuccessResult("??????????????????");
        } catch (Exception e) {
            logger.error("addOrUpdatePermissionGroup error!", e);
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "??????????????????????????????");
        }
    }

    /**
     * sys_key ????????? permit_key
     *
     * @param list
     * @return
     */
    private List<String> changeKey(List<String> list) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        List<String> permissions = permitItemPositionMapper.getPermission(list);
        List<String> result = new ArrayList<>();
        for (String str : permissions) {
            //?????????????????????
            result.addAll(funcJsonTreeBp.concatData(str));
        }
        return result;
    }

    @Override
    public ResultVO getPermissionGroupInfo(String jsonStr) {
        try {
            /* 1??????json???????????????Json?????? */
            JSONObject jsonObject = StringUtility.parseString(jsonStr).getJSONObject("data");
            Integer sysType = jsonObject.getInteger("sysType");
            if (null == sysType) {
                sysType = 0;
            }
            //?????????id
            String groupId = jsonObject.getString("groupId");
            if (StringUtil.isEmpty(groupId)) {
                throw new URCBizException("groupId????????????", ErrorCode.E_000003);
            }
            PositionGroupInfo result = new PositionGroupInfo();
            //????????????id?????????
            String name = positionGroupMapper.getPermissionGroupName(groupId);
            result.setGroupId(groupId);
            result.setGroupName(name);
            result.setSysType(sysType);
            //??????????????????
            List<UserByPosition> positions = positionGroupMapper.getPositions(groupId);
            result.setPositions(positions);
            //??????????????????
            List<PermissionVO> selectedContext = positionGroupMapper.getSelectedContext(groupId, sysType);
            result.setSelectedContext(selectedContext);
            return VoHelper.getSuccessResult(result);
        } catch (Exception e) {
            logger.error("getPermissionGroupInfo error!", e);
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "???????????????????????????");
        }
    }

    @Override
    public ResultVO getPositionList(String jsonStr) {
        try {
            /* 1??????json???????????????Json?????? */
            JSONObject jsonObject = StringUtility.parseString(jsonStr).getJSONObject("data");
            //???????????????
            String positionName = null;
            if (null != jsonObject) {
                positionName = jsonObject.getString("positionName");
            }
            List<UserByPosition> positions = positionGroupMapper.getPositionList(positionName);
            return VoHelper.getSuccessResult(positions);
        } catch (Exception e) {
            logger.error("getPositionList error!", e);
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "????????????????????????");
        }
    }

    @Override
    public ResultVO getPositionPermission(String jsonStr) {
        try {
            /* 1??????json???????????????Json?????? */
            JSONObject jsonObject = StringUtility.parseString(jsonStr).getJSONObject("data");
            //??????id
            String positionId = jsonObject.getString("positionId");
            if (StringUtil.isEmpty(positionId)) {
                throw new URCBizException("positionId????????????", ErrorCode.E_000003);
            }
            List<PermissionVO> permissionVO = positionGroupMapper.getPositionPermission(positionId);
            return VoHelper.getSuccessResult(permissionVO);
        } catch (Exception e) {
            logger.error("getPositionPermission error!", e);
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "?????????????????????????????????");
        }

    }

    @Override
    public ResultVO getPositionInfoByPermitKey(String jsonStr) {
        try {
            /* 1??????json???????????????Json?????? */
            JSONObject jsonObject = StringUtility.parseString(jsonStr).getJSONObject("data");
            //???????????????
            String lstPermitKeyStr = jsonObject.getString("lstPermitKey");
            List<String> lstPermitKey = JSONArray.parseArray(lstPermitKeyStr, String.class);
            //??????????????????
            if (CollectionUtils.isEmpty(lstPermitKey) || lstPermitKey.size() < 1 || lstPermitKey.size() > 5) {
                return VoHelper.getErrorResult(ErrorCode.E_000003.getState(), "????????????1-5??????????????? ");
            }
            //?????????
            String positionIdStr = jsonObject.getString("positionIds");
            List<String> positionIds = JSONArray.parseArray(positionIdStr, String.class);
            /*??????????????????queryMap*/
            Map<String, Object> queryMap = new HashMap<>();
            int pageNumber = jsonObject.getInteger("pageNumber");
            int pageData = jsonObject.getInteger("pageData");
            if (!StringUtil.isNum(pageNumber) || !StringUtil.isNum(pageData)) {
                throw new URCBizException("pageNumber or  pageData is not a num", ErrorCode.E_000003);
            }
            int currPage = pageNumber;
            int pageSize = pageData;
            queryMap.put("currIndex", (currPage - 1) * pageSize);
            queryMap.put("pageSize", pageSize);
            queryMap.put("lstPermitKey", lstPermitKey);
            queryMap.put("positionIds", positionIds);
            //????????????
            List<UserByPosition> list = positionGroupMapper.getPositionInfoByPermitKey(queryMap);
            //????????????
            int total = positionGroupMapper.getPositionInfoByPermitKeyCount(queryMap);
            PageResultVO pageResultVO = new PageResultVO(list, total, queryMap.get("pageSize").toString());
            return VoHelper.getSuccessResult(pageResultVO);
        } catch (Exception e) {
            logger.error("getPermissionGroupByUser error!", e);
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "??????????????????????????????");
        }
    }

    @Override
    public ResultVO exportPositionInfoByPermitKey(String jsonStr) {
        try {
            /* 1??????json???????????????Json?????? */
            JSONObject jsonObject = StringUtility.parseString(jsonStr).getJSONObject("data");
            //???????????????
            String lstPermitKeyStr = jsonObject.getString("lstPermitKey");
            List<String> lstPermitKey = JSONArray.parseArray(lstPermitKeyStr, String.class);
            //??????????????????
            if (CollectionUtils.isEmpty(lstPermitKey) || lstPermitKey.size() < 1 || lstPermitKey.size() > 5) {
                return VoHelper.getErrorResult(ErrorCode.E_000003.getState(), "????????????1-5??????????????? ");
            }
            //?????????
            String positionIdStr = jsonObject.getString("positionIds");
            List<String> positionIds = JSONArray.parseArray(positionIdStr, String.class);
            Map<String, Object> queryMap = new HashMap<>();
            queryMap.put("lstPermitKey", lstPermitKey);
            queryMap.put("positionIds", positionIds);
            //????????????
            int total = positionGroupMapper.getPositionInfoByPermitKeyCount(queryMap);
            queryMap.put("currIndex", 0);
            queryMap.put("pageSize", total);
            List<UserByPosition> list = positionGroupMapper.getPositionInfoByPermitKey(queryMap);
            boolean flag = true;
            if (CollectionUtils.isEmpty(positionIds)) {
                flag = false;
            } else {
                flag = true;
            }
            String downloadFileUrl = downloadByData(list, flag);
            return VoHelper.getResultVO(CommonMessageCodeEnum.SUCCESS.getCode(), CommonMessageCodeEnum.SUCCESS.getDesc(), downloadFileUrl);
        } catch (Exception e) {
            logger.error(String.format("exportPositionInfoByPermitKey error ! json:%s", jsonStr), e);
        }
        return VoHelper.getErrorResult();
    }

    @Override
    public ResultVO exportPositionPower(String jsonStr) {
        try {
            String exportFlag = configBp.getString(POSITION_EXPORT_FLAG);
            if(!"1".equals(exportFlag)){
                return VoHelper.getErrorResult(ErrorCode.E_000000.getState(),"?????????????????????");
            }
            /* 1??????json???????????????Json?????? */
            JSONObject jsonObject = StringUtility.parseString(jsonStr).getJSONObject("data");
            /* 2???????????????????????? */
            String positionIdStr = jsonObject.getString("positionIds");
            List<String> positionIds = null;
            if(null != positionIdStr) {
                 positionIds = JSONArray.parseArray(positionIdStr, String.class);
            }else {
                 positionIds = getPositionIdList(jsonStr);
            }
            //??????????????????
            List<PositionPower> list = positionGroupMapper.positionPowerList(positionIds);
            //??????????????????
            String downloadFileUrl = downloadPower(list);
            return VoHelper.getResultVO(CommonMessageCodeEnum.SUCCESS.getCode(), CommonMessageCodeEnum.SUCCESS.getDesc(), downloadFileUrl);
        } catch (Exception e) {
            logger.error(String.format("exportPositionPower error ! json:%s", jsonStr), e);
        }
        return VoHelper.getErrorResult();
    }

    /**
     * ??????excle
     *
     * @param list
     * @return
     */
    private String downloadPower(List<PositionPower> list) {
        Date now = new Date();
        String fileName = excelTemp + "positonPower-" + now.getTime() + ".xlsx";
        positionPowerExcelExport.setList(list);
        positionPowerExcelExport.setExportFilePath(fileName);
        positionPowerExcelExport.initExportExcel();
        String result = FileUpDownLoadUtils.getDownloadUrl("http://www.soter.youkeshu.com/yks/file/server/", fileName);
        return result;
    }


    private List<String> getPositionIdList(String jsonStr) {
        /* 1??????json???????????????Json?????? */
        JSONObject jsonObject = StringUtility.parseString(jsonStr).getJSONObject("data");
        /* 2???????????????????????? */
        String operator = StringUtility.parseString(jsonStr).getString("operator");
        // ??????????????????(0 .????????????,1.?????????)
        Integer isAuthorizable = jsonObject.getInteger("isAdmin");
        //????????????(0.?????????,1.??????)
        Integer isActive = jsonObject.getInteger("isActive");
        // ????????????(0.????????????,1.?????????,2.owner)
        Integer searchType = jsonObject.getInteger("searchType");
        //????????????,???????????? ','??????
        String searchContent = jsonObject.getString("searchContent");
        //1:?????? 2:??????
        Integer roleType = jsonObject.getInteger("roleType");
        if (StringUtil.isEmpty(operator)) {
            throw new URCBizException("parameter operator is null", ErrorCode.E_000002);
        }

        /*??????????????????queryMap*/
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("isAdmin", isAuthorizable);
        queryMap.put("isActive", isActive);
        queryMap.put("roleType", roleType);
        if (searchType != null) {
            switch (searchType) {
                case 0:
                    queryMap.put("roleNames", splitStr(searchContent));
                    break;
                case 1:
                    queryMap.put("createBys", splitStr(searchContent));
                    break;
                case 2:
                    queryMap.put("owners", splitStr(searchContent));
                    break;
                default:
            }
        }
        /*??????????????????????????????createBy????????????????????????????????????*/
        Boolean isAdmin = roleMapper.isSuperAdminAccount(operator);
        if (isAdmin) {
            queryMap.put("createBy", "");
            queryMap.put("roleIds", null);
        } else {
            //??????????????????????????????owner???roleId
            List<RoleOwnerDO> ownerDOS = ownerMapper.selectOwnerByOwner(operator);
            if (!CollectionUtils.isEmpty(ownerDOS)) {
                List<Long> roleIdList = new ArrayList<>();
                for (RoleOwnerDO ownerDO : ownerDOS) {
                    roleIdList.add(ownerDO.getRoleId());
                }
                queryMap.put("roleIds", roleIdList);
            }
            queryMap.put("createBy", operator);
        }
        List<RoleDO> roleDOS = roleMapper.listRoles(queryMap);
        if (!CollectionUtils.isEmpty(roleDOS)) {
            return roleDOS.stream().map(e -> e.getRoleId()+"").collect(Collectors.toList());
        }
        return null;
    }

    /**
     * split?????????????????????????????????
     *
     * @param
     * @return
     * @Author lwx
     * @Date 2018/11/6 14:40
     */
    private List<String> splitStr(String strSrc) {
        if (StringUtils.isEmpty(strSrc)) {
            return Collections.EMPTY_LIST;
        }
        String[] arrAcct = strSrc.split("(,)|(\r\n)|(\n)|(\r)");
        List<String> lstRslt = new ArrayList<>();
        for (int i = 0; i < arrAcct.length; i++) {
            String mem = StringUtility.trimPattern_Private(arrAcct[i], "\\s");
            if (!StringUtility.isNullOrEmpty(mem)) {
                lstRslt.add(mem);
            }
        }
        return lstRslt;
    }

    /**
     * ??????excle
     *
     * @param list
     * @return
     */
    private String downloadByData(List<UserByPosition> list, boolean flag) {
        Date now = new Date();
        String fileName = excelTemp + "positon-" + now.getTime() + ".xlsx";
        positionInfoExcelExport.setFlag(flag);
        positionInfoExcelExport.setList(list);
        positionInfoExcelExport.setExportFilePath(fileName);
        positionInfoExcelExport.initExportExcel();
        String result = FileUpDownLoadUtils.getDownloadUrl("http://www.soter.youkeshu.com/yks/file/server/", fileName);
        return result;
    }

    /**
     * ??????
     */
    private List<String> removeDuplicate(List<String> lstUserName) {
        HashSet h = new HashSet(lstUserName);
        lstUserName.clear();
        lstUserName.addAll(h);
        return lstUserName;
    }

    private void scanFunction(String parentName, List<FunctionVO> lstFunction, Set<FunctionVO> lstAllKey) {
        if (CollectionUtils.isEmpty(lstFunction)) {
            return;
        }
        for (FunctionVO f : lstFunction) {
            if (CollectionUtils.isEmpty(f.function)) {
                f.name = String.format("%s-%s", parentName, f.name);
                lstAllKey.add(f);
            } else {
                scanFunction(f.name, f.function, lstAllKey);
            }
        }
    }
}