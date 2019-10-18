package com.yks.urc.permitStat.bp.impl;

import com.yks.urc.entity.PermitRefreshTaskVO;
import com.yks.urc.enums.PermitRefreshTaskStatusEnum;
import com.yks.urc.enums.PermitRefreshTaskTypeEnum;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.mapper.IPermitRefreshTaskMapper;
import com.yks.urc.permitStat.bp.api.IPermitRefreshTaskBp;
import com.yks.urc.serialize.bp.api.ISerializeBp;
import com.yks.urc.session.bp.api.ISessionBp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PermitRefreshTaskBpImpl implements IPermitRefreshTaskBp {

    @Autowired
    private ISessionBp sessionBp;

    @Autowired
    private ISerializeBp serializeBp;
    @Autowired
    private IPermitRefreshTaskMapper permitRefreshTaskMapper;

    public void addPermitRefreshTask(List<String> lstUserName) {
        if (CollectionUtils.isEmpty(lstUserName)) {
            return;
        }
        List<String> userNameList = lstUserName.stream().distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(userNameList)) {
            return;
        }

        PermitRefreshTaskVO permitRefreshTaskVO = new PermitRefreshTaskVO();
        permitRefreshTaskVO.setTaskType(StringUtility.convertToInteger(PermitRefreshTaskTypeEnum.REFRESH_USER.getCode()));
        permitRefreshTaskVO.setTaskStatus(StringUtility.convertToByte(PermitRefreshTaskStatusEnum.WAIT_TO_DO.getCode()));
        permitRefreshTaskVO.setTaskParam(serializeBp.obj2Json(userNameList));
        permitRefreshTaskVO.setCreator(sessionBp.getOperator());
        permitRefreshTaskVO.setModifier(sessionBp.getOperator());
        permitRefreshTaskMapper.insertSelective(permitRefreshTaskVO);
    }

    @Override
    public void addPermitRefreshTaskForImportSysPermit(List<String> lstSysKey) {
        if (CollectionUtils.isEmpty(lstSysKey)) {
            return;
        }
        List<String> userNameList = lstSysKey.stream().distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(userNameList)) {
            return;
        }

        PermitRefreshTaskVO permitRefreshTaskVO = new PermitRefreshTaskVO();
        permitRefreshTaskVO.setTaskType(StringUtility.convertToInteger(PermitRefreshTaskTypeEnum.REFRESH_SYS.getCode()));
        permitRefreshTaskVO.setTaskStatus(StringUtility.convertToByte(PermitRefreshTaskStatusEnum.WAIT_TO_DO.getCode()));
        permitRefreshTaskVO.setTaskParam(serializeBp.obj2Json(userNameList));
        permitRefreshTaskVO.setCreator(sessionBp.getOperator());
        permitRefreshTaskVO.setModifier(sessionBp.getOperator());
        permitRefreshTaskMapper.insertSelective(permitRefreshTaskVO);
    }
}
