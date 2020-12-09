/*
 * 文件名：SystemServiceImpl.java
 * 版权：Copyright by www.youkeshu.com
 * 描述：
 * 创建人：zhouhuan
 * 创建时间：2020/12/1
 * 修改理由：
 * 修改内容：
 */
package com.yks.urc.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.yks.urc.constant.UrcConstant;
import com.yks.urc.entity.PermissionDO;
import com.yks.urc.entity.UrcSystemAdministrator;
import com.yks.urc.enums.CommonMessageCodeEnum;
import com.yks.urc.fw.DateUtil;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.mapper.IRoleMapper;
import com.yks.urc.mapper.PermissionMapper;
import com.yks.urc.mapper.UrcSystemAdministratorMapper;
import com.yks.urc.serialize.bp.api.ISerializeBp;
import com.yks.urc.service.api.ISystemService;
import com.yks.urc.vo.*;
import com.yks.urc.vo.helper.VoHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhouhuan
 * @version 1.0
 * @date 2020/12/1
 * @see SystemServiceImpl
 * @since JDK1.8
 */
@Component
public class SystemServiceImpl implements ISystemService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PermissionMapper permissionMapper;
    @Autowired
    private ISerializeBp serializeBp;
    @Autowired
    private UrcSystemAdministratorMapper urcSystemAdministratorMapper;
    @Autowired
    private IRoleMapper roleMapper;

    @Override
    public ResultVO getSystemList() {
        try {
            List<PermissionDO> allSystem = permissionMapper.getAllSystem();
            return VoHelper.getSuccessResult(allSystem);
        } catch (Exception e) {
            logger.error("getSystemList error!", e);
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "获取系统下拉框列表失败");
        }
    }

    @Override
    public ResultVO getSystemPermission(String jsonStr) {
        try {
            String sysKey = StringUtility.parseString(jsonStr).getJSONObject("data").getString("sysKey");
            if(StringUtility.isNullOrEmpty(sysKey)){
                return VoHelper.getErrorResult(CommonMessageCodeEnum.PARAM_NULL.getCode(), CommonMessageCodeEnum.PARAM_NULL.getDesc());
            }
            PermissionDO permissionDO = permissionMapper.getSystemKey(sysKey);
            return VoHelper.getSuccessResult(permissionDO);
        } catch (Exception e) {
            logger.error("getSystemPermission error!", e);
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "获取系统的功能权限失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO updateSystemInfo(String jsonStr){
        RequestVO<UpdateSystemVO> requestVO = serializeBp.json2ObjNew(jsonStr, new TypeReference<RequestVO<UpdateSystemVO>>() {
        });

        //判断用户是不是超级管理员
       /* boolean isSuperAdmin = roleMapper.isSuperAdminAccount(requestVO.operator);
        if(!isSuperAdmin){
            VoHelper.getErrorResult(CommonMessageCodeEnum.PARAM_INVALID.getCode(),"当前用户不是超级管理员,无法进行操作");
        }*/

        UpdateSystemVO updateSystemVO = requestVO.data;
        if (StringUtility.isNullOrEmpty(updateSystemVO.sysKey)) {
            return VoHelper.getErrorResult(CommonMessageCodeEnum.PARAM_NULL.getCode(), "系统key不能为空");
        }
        PermissionDO p = new PermissionDO();
        p.setSysKey(updateSystemVO.sysKey);
        p.setStatus(updateSystemVO.status);
        p.setRemark(updateSystemVO.remark == null ? "": updateSystemVO.remark);
        p.setModifiedBy(requestVO.operator);
        permissionMapper.updateSysContextBySysKeyCondition(p);

        //先删除在插入数据
        urcSystemAdministratorMapper.deleteBySysKey(updateSystemVO.sysKey);
        List<UrcSystemAdministrator> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(updateSystemVO.dataAdministrators)) {
            for (String dataAdministrator : updateSystemVO.dataAdministrators) {
                UrcSystemAdministrator urcSystemAdministrator = new UrcSystemAdministrator();
                urcSystemAdministrator.setSysKey(updateSystemVO.sysKey);
                urcSystemAdministrator.setType(UrcConstant.AdministratorType.dataAdministrator);
                urcSystemAdministrator.setUserName(dataAdministrator);
                urcSystemAdministrator.setCreator(requestVO.operator);
                urcSystemAdministrator.setCreateTime(new Date());
                urcSystemAdministrator.setModifier(requestVO.operator);
                urcSystemAdministrator.setModifiedTime(new Date());
                list.add(urcSystemAdministrator);
            }
        }

        if (!CollectionUtils.isEmpty(updateSystemVO.functionAdministrators)) {
            for (String dataAdministrator : updateSystemVO.functionAdministrators) {
                UrcSystemAdministrator urcSystemAdministrator = new UrcSystemAdministrator();
                urcSystemAdministrator.setSysKey(updateSystemVO.sysKey);
                urcSystemAdministrator.setType(UrcConstant.AdministratorType.functionAdministrator);
                urcSystemAdministrator.setUserName(dataAdministrator);
                urcSystemAdministrator.setCreator(requestVO.operator);
                urcSystemAdministrator.setCreateTime(new Date());
                urcSystemAdministrator.setModifier(requestVO.operator);
                urcSystemAdministrator.setModifiedTime(new Date());
                list.add(urcSystemAdministrator);
            }
        }

        if (!CollectionUtils.isEmpty(list)) {
            urcSystemAdministratorMapper.insertBatch(list);
        }
        return VoHelper.getSuccessResult();
    }

    @Override
    public ResultVO getSystemInfoList(String jsonStr) {
        try {
            JSONObject jsonObject = StringUtility.parseString(jsonStr);
            JSONObject data = jsonObject.getJSONObject("data");
            Integer pageNumber = data.getInteger("pageNumber");
            Integer pageData = data.getInteger("pageData");
            Integer status = data.getInteger("status");
            String sysKey = data.getString("sysKey");
            if(pageNumber == null){
                pageNumber =1;
            }
            if(pageData == null){
                pageData = 20;
            }
            Map<String,Object> resultMap = new HashMap<>();
            //分页查询系统信息
            List<PermissionDO> permissionDOS = permissionMapper.selectSystemInfoPage(sysKey,status,pageData,(pageNumber-1)*pageData);
            //查询总数
            List<PermissionDO> list = permissionMapper.selectSystemInfoPage(sysKey, status, null, null);
            if(CollectionUtils.isEmpty(permissionDOS)){
                resultMap.put("lst",Collections.EMPTY_LIST);
                resultMap.put("total",0);
                return VoHelper.getSuccessResult(resultMap);
            }
            List<String> sysKeys = permissionDOS.stream().map(PermissionDO::getSysKey).collect(Collectors.toList());
            //根据系统key查询系统管理员对应人员
            List<UrcSystemAdministrator> urcSystemAdministrators = urcSystemAdministratorMapper.selectBySysKey(sysKeys);
            //根据系统key将各系统对应管理员分组
            Map<String, List<UrcSystemAdministrator>> listMap = urcSystemAdministrators.stream().collect(Collectors.groupingBy(UrcSystemAdministrator::getSysKey));
            List<UpdateSystemVO> lst = new ArrayList<>();
            for (PermissionDO aDo : permissionDOS) {
                UpdateSystemVO updateSystemVO = new UpdateSystemVO();
                updateSystemVO.remark = aDo.getRemark();
                updateSystemVO.status = aDo.getStatus();
                updateSystemVO.isInternalSystem = aDo.getIsInternalSystem();
                updateSystemVO.sysKey = aDo.getSysKey();
                updateSystemVO.sysName = aDo.getSysName();
                updateSystemVO.createdTime = aDo.getCreateTime() != null ? DateUtil.formatDate(aDo.getCreateTime(), "yyyy-MM-dd HH:mm:ss") : null;
                updateSystemVO.dataAdministrators = Collections.EMPTY_LIST;
                updateSystemVO.functionAdministrators = Collections.EMPTY_LIST;
                //获取系统的管理人员
                List<UrcSystemAdministrator> urcSystemAdministratorList = listMap.get(aDo.getSysKey());
                if(!CollectionUtils.isEmpty(urcSystemAdministratorList)){
                    //按管理员类型分组
                    Map<Byte, List<UrcSystemAdministrator>> map = urcSystemAdministratorList.stream().collect(Collectors.groupingBy(UrcSystemAdministrator::getType));
                    for (Byte aByte : map.keySet()) {
                        List<UrcSystemAdministrator> administrators = map.get(aByte);
                        List<String> userList = administrators.stream().map(UrcSystemAdministrator::getUserName).collect(Collectors.toList());
                        //数据管理人员
                        if(StringUtility.stringEqualsIgnoreCaseObj(UrcConstant.AdministratorType.dataAdministrator,aByte)){
                            updateSystemVO.dataAdministrators = userList;
                        }
                        //功能权限管理人员
                        if(StringUtility.stringEqualsIgnoreCaseObj(UrcConstant.AdministratorType.functionAdministrator,aByte)){
                            updateSystemVO.functionAdministrators = userList;
                        }
                    }
                }
                lst.add(updateSystemVO);
            }

            resultMap.put("lst",lst);
            resultMap.put("total",list.size());
            return VoHelper.getSuccessResult(resultMap);
        } catch (Exception e) {
            logger.error("getSystemInfoList error!", e);
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "获取系统信息列表失败");
        }
    }
}
