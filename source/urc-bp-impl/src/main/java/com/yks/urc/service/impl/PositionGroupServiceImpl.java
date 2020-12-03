package com.yks.urc.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yks.urc.entity.PositionGroupVO;
import com.yks.urc.entity.RoleDO;
import com.yks.urc.entity.UrcGroupPermission;
import com.yks.urc.entity.UrcPositionGroup;
import com.yks.urc.enums.CommonMessageCodeEnum;
import com.yks.urc.exception.ErrorCode;
import com.yks.urc.exception.URCBizException;
import com.yks.urc.fw.StringUtil;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.mapper.IPositionGroupMapper;
import com.yks.urc.mapper.UrcGroupPermissionMapper;
import com.yks.urc.mapper.UrcPositionGroupMapper;
import com.yks.urc.seq.bp.api.ISeqBp;
import com.yks.urc.service.api.IPositionGroupService;
import com.yks.urc.vo.*;
import com.yks.urc.vo.helper.VoHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

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

    @Override
    public ResultVO getPermissionGroupByUser(String jsonStr) {
        try {
            /* 1、将json字符串转为Json对象 */
            JSONObject jsonObject = StringUtility.parseString(jsonStr).getJSONObject("data");
            //权限组名称
            String groupName = jsonObject.getString("groupName");
            //用户名
            String userName = jsonObject.getString("userName");
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
            queryMap.put("userName", userName);
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
            if (!StringUtil.isNum(groupId)) {
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
    public ResultVO addOrUpdatePermissionGroup(String jsonStr) {
        try {
            /* 1、将json字符串转为Json对象 */
            JSONObject jsonObject = StringUtility.parseString(jsonStr).getJSONObject("data");
            String operator = jsonObject.getString("operator");
            //权限组id
            String groupId = jsonObject.getString("groupId");
            String groupName = jsonObject.getString("groupName");
            String positionIdStr = jsonObject.getString("positionIds");
            List<Long> positionIds = JSONArray.parseArray(positionIdStr, Long.class);
            String selectedContext = jsonObject.getString("selectedContext");
            List<Map> selectedContextmap = JSONArray.parseArray(selectedContext, Map.class);
            //校验岗位是不是包含超管岗位
            boolean existSuperAdmin = positionGroupMapper.existSuperAdmin(positionIds);
            if(!existSuperAdmin) {
                Long newGroupId = null;
                if (StringUtils.isEmpty(groupId)) {
                     newGroupId = seqBp.getNextRoleId();
                }else {
                    newGroupId = Long.parseLong(groupId);
                    //删除旧数据
                    urcPositionGroupMapper.deleteByGroupId(newGroupId);
                    urcGroupPermissionMapper.deleteByGroupId(newGroupId);
                }
                if(positionIds != null && positionIds.size() > 0){
                    for(Long positionId:positionIds) {
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
                    }
                }
                if(selectedContextmap != null && selectedContextmap.size() > 0) {
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

    @Override
    public ResultVO getPermissionGroupInfo(String jsonStr) {
        try {
            /* 1、将json字符串转为Json对象 */
            JSONObject jsonObject = StringUtility.parseString(jsonStr).getJSONObject("data");
            //权限组id
            String groupId = jsonObject.getString("groupId");
            if (!StringUtil.isNum(groupId)) {
                throw new URCBizException("groupId不能为空", ErrorCode.E_000003);
            }
            PositionGroupInfo result = new PositionGroupInfo();
            //获的权限id和名称
            String name = positionGroupMapper.getPermissionGroupName(groupId);
            result.setGroupId(groupId);
            result.setGroupName(name);
            //获得岗位信息
            List<UserByPosition> positions = positionGroupMapper.getPositions(groupId);
            result.setPositions(positions);
            //获得权限信息
            List<PermissionVO> selectedContext = positionGroupMapper.getSelectedContext(groupId);
            result.setSelectedContext(selectedContext);
            return VoHelper.getSuccessResult(result);
        } catch (Exception e) {
            logger.error("getPermissionGroupInfo error!", e);
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "获取权限组详情失败");
        }
    }
}


