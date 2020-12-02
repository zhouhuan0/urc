package com.yks.urc.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yks.urc.entity.PositionGroupVO;
import com.yks.urc.enums.CommonMessageCodeEnum;
import com.yks.urc.exception.ErrorCode;
import com.yks.urc.exception.URCBizException;
import com.yks.urc.fw.StringUtil;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.mapper.IPositionGroupMapper;
import com.yks.urc.service.api.IPositionGroupService;
import com.yks.urc.vo.PageResultVO;
import com.yks.urc.vo.ResultVO;
import com.yks.urc.vo.helper.VoHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PositionGroupServiceImpl implements IPositionGroupService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    IPositionGroupMapper positionGroupMapper;


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
}


