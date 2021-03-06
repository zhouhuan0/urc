
package com.yks.urc.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yks.urc.entity.CsPlatform;
import com.yks.urc.entity.CsPlatformGroup;
import com.yks.urc.exception.ErrorCode;
import com.yks.urc.exception.URCBizException;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.mapper.CsPlatformGroupMapper;
import com.yks.urc.mapper.CsPlatformMapper;
import com.yks.urc.service.api.ICsService;
import com.yks.urc.vo.ResultVO;
import com.yks.urc.vo.helper.VoHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class CsServiceImpl implements ICsService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CsPlatformGroupMapper csPlatformGroupMapper;

    @Autowired
    private CsPlatformMapper csPlatformMapper;

    @Transactional
    public ResultVO addCsUserGroup(String json) {
        JSONObject jsonObject = StringUtility.parseString(json);
        String centerPlatformId=jsonObject.getString("center_platform_id");
        String platformName=jsonObject.getString("platformName");
        String groupId=jsonObject.getString("groupId");
        String groupName=jsonObject.getString("groupName");
        String operator = jsonObject.getString("operator");
        if(StringUtility.isNullOrEmpty(centerPlatformId)||StringUtility.isNullOrEmpty(groupId)){
            throw new URCBizException(ErrorCode.E_000003.getState(), "新增客服分组参数为空");
        }

        if(csPlatformGroupMapper.selectByGroupId(groupId)!=null){
            throw new URCBizException(ErrorCode.E_000003.getState(), String.format("新增客服分组参数已经存在platformId=%s,groupId=%s", centerPlatformId,groupId));
        }

        CsPlatformGroup csPlatformGroup = new CsPlatformGroup();
        csPlatformGroup.setCreateBy(operator);
        csPlatformGroup.setModifiedBy(operator);
        csPlatformGroup.setCreateTime(new Date());
        csPlatformGroup.setModifiedTime(new Date());
        csPlatformGroup.setGroupId(groupId);
        csPlatformGroup.setPlatformId(centerPlatformId);
        csPlatformGroup.setGroupName(groupName);
        csPlatformGroupMapper.insertSelective(csPlatformGroup);

        if(csPlatformMapper.selectByPlatformId(centerPlatformId)==null){
            CsPlatform csPlatform=new CsPlatform();
            csPlatform.setCreateBy(operator);
            csPlatform.setCreateTime(new Date());
            csPlatform.setModifiedBy(operator);
            csPlatform.setModifiedTime(new Date());
            csPlatform.setPlatformId(centerPlatformId);
            csPlatform.setPlatformName(platformName);
            csPlatformMapper.insertSelective(csPlatform);
        }

        return VoHelper.getSuccessResult();
    }

    @Override
    public ResultVO editCsUserGroup(String json) {
        JSONObject jsonObject = StringUtility.parseString(json);
        String centerPlatformId=jsonObject.getString("center_platform_id");
        String groupId=jsonObject.getString("groupId");
        String groupName=jsonObject.getString("groupName");
        String operator = jsonObject.getString("operator");
        if(StringUtility.isNullOrEmpty(centerPlatformId)||StringUtility.isNullOrEmpty(groupId)){
            throw new URCBizException(ErrorCode.E_000003.getState(), "编辑客服分组参数为空");
        }
        CsPlatformGroup csPlatformGroup= csPlatformGroupMapper.selectByPlantIdAndGroupId(centerPlatformId,groupId);
        if(csPlatformGroup==null){
            throw new URCBizException(ErrorCode.E_000003.getState(), String.format("编辑客服分组参数不存在platformId=%s,groupId=%s", centerPlatformId,groupId));
        }
        csPlatformGroup.setGroupName(groupName);
        csPlatformGroup.setModifiedTime(new Date());
        csPlatformGroup.setModifiedBy(operator);
        csPlatformGroupMapper.updateByPrimaryKeySelective(csPlatformGroup);
        return VoHelper.getSuccessResult();
    }

    @Override
    public ResultVO delCsUserGroup(String json) {
        JSONObject jsonObject = StringUtility.parseString(json);
        String groupId=jsonObject.getString("groupId");
        if(StringUtility.isNullOrEmpty(groupId)){
            throw new URCBizException(ErrorCode.E_000003.getState(), "删除客服分组参数为空");
        }
        if(csPlatformGroupMapper.selectByGroupId(groupId)==null){
            throw new URCBizException(ErrorCode.E_000003.getState(), String.format("删除客服分组参数不存在groupId=%s",groupId));
        }
        csPlatformGroupMapper.deleteByGroupId(groupId);
        return VoHelper.getSuccessResult();
    }
}
