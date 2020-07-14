package com.yks.urc.role.bp.impl;

import com.yks.urc.entity.RoleOperLogVOWithBLOBs;
import com.yks.urc.enums.RoleLogEnum;
import com.yks.urc.mapper.IRoleOperLogMapper;
import com.yks.urc.role.bp.api.IRoleLogBp;
import com.yks.urc.session.bp.api.ISessionBp;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RoleLogBpImpl implements IRoleLogBp {

    @Autowired
    private IRoleOperLogMapper roleOperLogMapper;

    @Autowired
    private ISessionBp sessionBp;

    @Override
    public void addLog(List<Long> roleIds, RoleLogEnum roleLogEnum, String reqJson) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return;
        }
        List<RoleOperLogVOWithBLOBs> lstToAdd =
                roleIds.stream().map(r -> {
                    RoleOperLogVOWithBLOBs log = new RoleOperLogVOWithBLOBs();
                    log.setRoleId(r);
                    log.setLogger(roleLogEnum.getCode());
                    log.setReqBody(reqJson);
                    log.setCreater(sessionBp.getOperator());
                    log.setStackTrace(ExceptionUtils.getStackTrace(new Exception("LOG")));
                    return log;
                }).collect(Collectors.toList());
        roleOperLogMapper.insertBatch(lstToAdd);
    }
}
