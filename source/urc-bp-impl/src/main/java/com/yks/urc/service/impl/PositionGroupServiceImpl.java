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
    //是否导出岗位权限(1可以导出，0不能)
    private final static String POSITION_EXPORT_FLAG = "POSITION_EXPORT_FLAG";

    private static String excelTemp = "/opt/tmp/";

    @Override
    public ResultVO getPermissionGroupByUser(String jsonStr, String operator) {
        try {
            /* 1、将json字符串转为Json对象 */
            JSONObject jsonObject = StringUtility.parseString(jsonStr).getJSONObject("data");
            //权限组名称
            String groupName = jsonObject.getString("groupName");
            //用户名
            if (StringUtil.isEmpty(operator)) {
                throw new URCBizException("operator is empty", ErrorCode.E_000003);
            }
            /*组装查询条件queryMap*/
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
            //获得数据
            List<PositionGroupVO> list = positionGroupMapper.getPermissionGroupByUser(queryMap);
            //获得总数
            int total = positionGroupMapper.getPermissionGroupByUserCount(queryMap);
            PageResultVO pageResultVO = new PageResultVO(list, total, queryMap.get("pageSize").toString());
            return VoHelper.getSuccessResult(pageResultVO);
        } catch (Exception e) {
            logger.error("getPermissionGroupByUser error!", e);
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "获取用户的权限组失败");
        }

    }

    @Override
    public ResultVO deletePermissionGroup(String jsonStr) {
        try {
            /* 1、将json字符串转为Json对象 */
            JSONObject jsonObject = StringUtility.parseString(jsonStr).getJSONObject("data");
            //权限组id
            String groupId = jsonObject.getString("groupId");
            if (StringUtil.isEmpty(groupId)) {
                throw new URCBizException("groupId不能为空", ErrorCode.E_000003);
            }
            //获得数据
            int num = positionGroupMapper.deletePermissionGroup(groupId);
            return VoHelper.getSuccessResult();
        } catch (Exception e) {
            logger.error("deletePermissionGroup error!", e);
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "通过groupId删除权限组失败");
        }
    }

    @Override
    public ResultVO addOrUpdatePermissionGroup(String jsonStr, String operator) {
        try {
            /* 1、将json字符串转为Json对象 */
            JSONObject jsonObject = StringUtility.parseString(jsonStr).getJSONObject("data");
            Integer sysType = jsonObject.getInteger("sysType");
            //权限组id
            String groupId = jsonObject.getString("groupId");
            String groupName = jsonObject.getString("groupName");
            String positionIdStr = jsonObject.getString("positionIds");
            List<Long> positionIds = JSONArray.parseArray(positionIdStr, Long.class);
            String selectedContext = jsonObject.getString("selectedContext");
            List<PermissionDO> permissionDOList = serializeBp.json2ObjNew(jsonObject.getString("selectedContext"), new TypeReference<List<PermissionDO>>() {
            });
            List<Map> selectedContextmap = JSONArray.parseArray(selectedContext, Map.class);
            List<String> roleSysKey = new ArrayList<>();
            //查询用户可以授权的系统
            boolean isSuperAdmin = roleMapper.isSuperAdminAccount(operator);
            if (!isSuperAdmin) {
                String roleId = configBp.getString("special_position");
                if(!StringUtility.isNullOrEmpty(roleId) && userRoleMapper.existsUserName(roleId,operator)){
                    roleSysKey = rolePermissionMapper.getPositionPermission(roleId,sysType == null ? null : sysType.toString()).stream().map(PermissionDO :: getSysKey).collect(Collectors.toList());
                }else{
                    roleSysKey = urcSystemAdministratorMapper.selectSysKeyByAdministratorType(operator, UrcConstant.AdministratorType.functionAdministrator.intValue(), sysType);
                }

                if (CollectionUtils.isEmpty(roleSysKey)) {
                    return VoHelper.getFail("当前用户没有可分配的系统功能权限");
                }
            } else {
                //超管也只能一个个系统处理
                roleSysKey = permitItemPositionMapper.findOneSystemKey(sysType);
            }

            //校验岗位是不是包含超管岗位
            boolean existSuperAdmin = true;
            if (!CollectionUtils.isEmpty(positionIds)) {
                existSuperAdmin = positionGroupMapper.existSuperAdmin(positionIds);
            } else {
                //岗位信息为空不需要判断超管
                existSuperAdmin = false;
            }
            if (!existSuperAdmin) {
                Long newGroupId = null;
                if (StringUtils.isEmpty(groupId)) {
                    //校验权限组是否存在
                    boolean isExist = urcPositionGroupMapper.existName(groupName);
                    if (isExist) {
                        return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "权限组名重复！");
                    }
                    newGroupId = seqBp.getNextRoleId();
                } else {
                    newGroupId = Long.parseLong(groupId);
                    //删除旧数据
                    urcPositionGroupMapper.deleteByGroupId(newGroupId);
                    //通过可以删除
                    //urcGroupPermissionMapper.deleteByGroupId(newGroupId);
                    urcGroupPermissionMapper.deleteByGroupIdandKey(newGroupId, roleSysKey);
                }
                List<String> positionList = new ArrayList<String>();
                if (positionIds != null && positionIds.size() > 0) {
                    for (Long positionId : positionIds) {
                        //入库权限分组表
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
                    //岗位为空也要加入一条
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
                        //入库权限组功能
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
                //保存操作日志
                UrcLog urcLog = new UrcLog(sessionBp.getOperator(), ModuleCodeEnum.ROLE_MANAGERMENT.getStatus(), "权限组保存或更新", groupName, jsonStr);
                iUrcLogBp.insertUrcLog(urcLog);
                //权限控制
                //先删除岗位权限,在插入
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
                            //清空集合
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
                        //拼接权限字符串
                        list.addAll(funcJsonTreeBp.concatData(rolePermissionDO.getSelectedContext()));
                    }
                    //写入关联表
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
                        //sys_key 转换成 permit_key
                        List<String> permitKeys = changeKey(roleSysKey);
                        //先删后插
                        permitItemPositionMapper.deleteBypositionIdAndKey(Long.parseLong(positionId), permitKeys);
                        permitItemPositionMapper.insertPosition(param);
                    }

                    //获取角色原关联的用户userName
                    UserRoleDO userRoleDO = new UserRoleDO();
                    userRoleDO.setRoleId(Long.parseLong(positionId));
                    List<String> oldRelationUsers = userRoleMapper.getUserNameByRoleId(userRoleDO);
                    //更新用户功能权限缓存
                    List<String> lstUserName = new ArrayList<>();
                    //添加角色原来关联的用户列表
                    if (oldRelationUsers != null && !oldRelationUsers.isEmpty()) {
                        lstUserName.addAll(oldRelationUsers);
                    }
                    if (!lstUserName.isEmpty()) {
                        /*去重*/
                        lstUserName = removeDuplicate(lstUserName);
                        //保存权限改变的用户
                        // 改为由定时任务执行
                        permitRefreshTaskBp.addPermitRefreshTask(lstUserName);
                    }
                }
                return VoHelper.getSuccessResult();
            }
            return VoHelper.getSuccessResult("存在超管岗位");
        } catch (Exception e) {
            logger.error("addOrUpdatePermissionGroup error!", e);
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "添加或更新权限组失败");
        }
    }

    /**
     * sys_key 转换成 permit_key
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
            //拼接权限字符串
            result.addAll(funcJsonTreeBp.concatData(str));
        }
        return result;
    }

    @Override
    public ResultVO getPermissionGroupInfo(String jsonStr) {
        try {
            /* 1、将json字符串转为Json对象 */
            JSONObject jsonObject = StringUtility.parseString(jsonStr).getJSONObject("data");
            Integer sysType = jsonObject.getInteger("sysType");
            if (null == sysType) {
                sysType = 0;
            }
            //权限组id
            String groupId = jsonObject.getString("groupId");
            if (StringUtil.isEmpty(groupId)) {
                throw new URCBizException("groupId不能为空", ErrorCode.E_000003);
            }
            PositionGroupInfo result = new PositionGroupInfo();
            //获的权限id和名称
            String name = positionGroupMapper.getPermissionGroupName(groupId);
            result.setGroupId(groupId);
            result.setGroupName(name);
            result.setSysType(sysType);
            //获得岗位信息
            List<UserByPosition> positions = positionGroupMapper.getPositions(groupId);
            result.setPositions(positions);
            //获得权限信息
            List<PermissionVO> selectedContext = positionGroupMapper.getSelectedContext(groupId, sysType);
            result.setSelectedContext(selectedContext);
            return VoHelper.getSuccessResult(result);
        } catch (Exception e) {
            logger.error("getPermissionGroupInfo error!", e);
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "获取权限组详情失败");
        }
    }

    @Override
    public ResultVO getPositionList(String jsonStr) {
        try {
            /* 1、将json字符串转为Json对象 */
            JSONObject jsonObject = StringUtility.parseString(jsonStr).getJSONObject("data");
            //权限组名称
            String positionName = null;
            if (null != jsonObject) {
                positionName = jsonObject.getString("positionName");
            }
            List<UserByPosition> positions = positionGroupMapper.getPositionList(positionName);
            return VoHelper.getSuccessResult(positions);
        } catch (Exception e) {
            logger.error("getPositionList error!", e);
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "获取岗位列表失败");
        }
    }

    @Override
    public ResultVO getPositionPermission(String jsonStr) {
        try {
            /* 1、将json字符串转为Json对象 */
            JSONObject jsonObject = StringUtility.parseString(jsonStr).getJSONObject("data");
            //岗位id
            String positionId = jsonObject.getString("positionId");
            if (StringUtil.isEmpty(positionId)) {
                throw new URCBizException("positionId不能为空", ErrorCode.E_000003);
            }
            List<PermissionVO> permissionVO = positionGroupMapper.getPositionPermission(positionId);
            return VoHelper.getSuccessResult(permissionVO);
        } catch (Exception e) {
            logger.error("getPositionPermission error!", e);
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "获取岗位的功能权限失败");
        }

    }

    @Override
    public ResultVO getPositionInfoByPermitKey(String jsonStr) {
        try {
            /* 1、将json字符串转为Json对象 */
            JSONObject jsonObject = StringUtility.parseString(jsonStr).getJSONObject("data");
            //权限组名称
            String lstPermitKeyStr = jsonObject.getString("lstPermitKey");
            List<String> lstPermitKey = JSONArray.parseArray(lstPermitKeyStr, String.class);
            //查询数量控制
            if (CollectionUtils.isEmpty(lstPermitKey) || lstPermitKey.size() < 1 || lstPermitKey.size() > 5) {
                return VoHelper.getErrorResult(ErrorCode.E_000003.getState(), "只能选择1-5个权限查询 ");
            }
            //用户名
            String positionIdStr = jsonObject.getString("positionIds");
            List<String> positionIds = JSONArray.parseArray(positionIdStr, String.class);
            /*组装查询条件queryMap*/
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
            //获得数据
            List<UserByPosition> list = positionGroupMapper.getPositionInfoByPermitKey(queryMap);
            //获得总数
            int total = positionGroupMapper.getPositionInfoByPermitKeyCount(queryMap);
            PageResultVO pageResultVO = new PageResultVO(list, total, queryMap.get("pageSize").toString());
            return VoHelper.getSuccessResult(pageResultVO);
        } catch (Exception e) {
            logger.error("getPermissionGroupByUser error!", e);
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "获取用户的权限组失败");
        }
    }

    @Override
    public ResultVO exportPositionInfoByPermitKey(String jsonStr) {
        try {
            /* 1、将json字符串转为Json对象 */
            JSONObject jsonObject = StringUtility.parseString(jsonStr).getJSONObject("data");
            //权限组名称
            String lstPermitKeyStr = jsonObject.getString("lstPermitKey");
            List<String> lstPermitKey = JSONArray.parseArray(lstPermitKeyStr, String.class);
            //查询数量控制
            if (CollectionUtils.isEmpty(lstPermitKey) || lstPermitKey.size() < 1 || lstPermitKey.size() > 5) {
                return VoHelper.getErrorResult(ErrorCode.E_000003.getState(), "只能选择1-5个权限查询 ");
            }
            //用户名
            String positionIdStr = jsonObject.getString("positionIds");
            List<String> positionIds = JSONArray.parseArray(positionIdStr, String.class);
            Map<String, Object> queryMap = new HashMap<>();
            queryMap.put("lstPermitKey", lstPermitKey);
            queryMap.put("positionIds", positionIds);
            //获得总数
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
                return VoHelper.getErrorResult(ErrorCode.E_000000.getState(),"此功能已关闭！");
            }
            /* 1、将json字符串转为Json对象 */
            JSONObject jsonObject = StringUtility.parseString(jsonStr).getJSONObject("data");
            /* 2、获取参数并校验 */
            String positionIdStr = jsonObject.getString("positionIds");
            List<String> positionIds = null;
            if(null != positionIdStr) {
                 positionIds = JSONArray.parseArray(positionIdStr, String.class);
            }else {
                 positionIds = getPositionIdList(jsonStr);
            }
            //获得导出集合
            List<PositionPower> list = positionGroupMapper.positionPowerList(positionIds);
            //生成导出文件
            String downloadFileUrl = downloadPower(list);
            return VoHelper.getResultVO(CommonMessageCodeEnum.SUCCESS.getCode(), CommonMessageCodeEnum.SUCCESS.getDesc(), downloadFileUrl);
        } catch (Exception e) {
            logger.error(String.format("exportPositionPower error ! json:%s", jsonStr), e);
        }
        return VoHelper.getErrorResult();
    }

    /**
     * 生成excle
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
        /* 1、将json字符串转为Json对象 */
        JSONObject jsonObject = StringUtility.parseString(jsonStr).getJSONObject("data");
        /* 2、获取参数并校验 */
        String operator = StringUtility.parseString(jsonStr).getString("operator");
        // 是否是管理员(0 .非管理员,1.管理员)
        Integer isAuthorizable = jsonObject.getInteger("isAdmin");
        //是否启用(0.不启用,1.启用)
        Integer isActive = jsonObject.getInteger("isActive");
        // 搜索类型(0.角色名称,1.创建人,2.owner)
        Integer searchType = jsonObject.getInteger("searchType");
        //搜索内容,多个值用 ','隔开
        String searchContent = jsonObject.getString("searchContent");
        //1:角色 2:岗位
        Integer roleType = jsonObject.getInteger("roleType");
        if (StringUtil.isEmpty(operator)) {
            throw new URCBizException("parameter operator is null", ErrorCode.E_000002);
        }

        /*组装查询条件queryMap*/
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
        /*超级管理员角色不需要createBy条件，可以查看所有的角色*/
        Boolean isAdmin = roleMapper.isSuperAdminAccount(operator);
        if (isAdmin) {
            queryMap.put("createBy", "");
            queryMap.put("roleIds", null);
        } else {
            //查出当前操作人所属的owner的roleId
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
     * split并去除前后空格，空元素
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
     * 生成excle
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
     * 去重
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