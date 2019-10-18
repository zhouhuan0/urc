package com.yks.urc.task;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yks.pls.task.quatz.BaseTask;
import com.yks.urc.entity.PermitRefreshTaskVO;
import com.yks.urc.enums.PermitRefreshTaskStatusEnum;
import com.yks.urc.enums.PermitRefreshTaskTypeEnum;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.mapper.IPermitRefreshTaskMapper;
import com.yks.urc.mapper.PermissionMapper;
import com.yks.urc.permitStat.bp.api.IPermitInverseQueryBp;
import com.yks.urc.permitStat.bp.api.IPermitStatBp;
import com.yks.urc.serialize.bp.api.ISerializeBp;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;


@Component
public class PermitRefreshTask extends BaseTask {
    private static final Logger logger = LoggerFactory.getLogger(PermitRefreshTask.class);

    @Autowired
    private IPermitInverseQueryBp permitInverseQueryBp;

    //    @Scheduled(cron = "0/59 * * * * ?")
//    public void doTaskSub() {
//        permitInverseQueryBp.doTaskSub();
//    }


    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private IPermitRefreshTaskMapper permitRefreshTaskMapper;

    @Autowired
    private ISerializeBp serializeBp;

    @Autowired
    IPermitStatBp permitStatBp;

    @Override
    protected void doTaskSub(String param) throws Exception {
        Integer pageSize = 1000;
        List<PermitRefreshTaskVO> lstToDo = permitRefreshTaskMapper.selectToDo(pageSize);
        if (CollectionUtils.isEmpty(lstToDo)) {
            return;
        }

        for (PermitRefreshTaskVO mem : lstToDo) {
            if (StringUtility.stringEqualsIgnoreCaseObj(PermitRefreshTaskTypeEnum.REFRESH_USER.getCode(), mem.getTaskType())) {
                doOne(mem);
            } else if (StringUtility.stringEqualsIgnoreCaseObj(PermitRefreshTaskTypeEnum.REFRESH_SYS.getCode(), mem.getTaskType())) {
                doTwo(mem);
            }
            permitRefreshTaskMapper.updateTaskStatus(mem);
        }
    }

    private void doTwo(PermitRefreshTaskVO mem) {
        try {
            if (StringUtils.isBlank(mem.getTaskParam())) {
                mem.setTaskStatus(StringUtility.convertToByte(PermitRefreshTaskStatusEnum.SUCCESS.getCode()));
            }
            List<String> lstSysKey = serializeBp.json2ObjNew(mem.getTaskParam(), new TypeReference<ArrayList<String>>() {
            });
            if (CollectionUtils.isEmpty(lstSysKey)) {
                mem.setTaskStatus(StringUtility.convertToByte(PermitRefreshTaskStatusEnum.SUCCESS.getCode()));
            } else {
                permitInverseQueryBp.updatePermitItemInfo(lstSysKey);
            }
            mem.setTaskStatus(StringUtility.convertToByte(PermitRefreshTaskStatusEnum.SUCCESS.getCode()));
        } catch (Exception ex) {
            mem.setTaskStatus(StringUtility.convertToByte(PermitRefreshTaskStatusEnum.FAILED.getCode()));
            logger.error(serializeBp.obj2Json(mem), ex);
        }
    }

    private void doOne(PermitRefreshTaskVO mem) {
        try {
            if (StringUtils.isBlank(mem.getTaskParam())) {
                mem.setTaskStatus(StringUtility.convertToByte(PermitRefreshTaskStatusEnum.SUCCESS.getCode()));
            }
            List<String> lstUserName = serializeBp.json2ObjNew(mem.getTaskParam(), new TypeReference<ArrayList<String>>() {
            });
            if (CollectionUtils.isEmpty(lstUserName)) {
                mem.setTaskStatus(StringUtility.convertToByte(PermitRefreshTaskStatusEnum.SUCCESS.getCode()));
            } else {
                permitInverseQueryBp.doTaskSub(lstUserName);
            }
            mem.setTaskStatus(StringUtility.convertToByte(PermitRefreshTaskStatusEnum.SUCCESS.getCode()));
        } catch (Exception ex) {
            mem.setTaskStatus(StringUtility.convertToByte(PermitRefreshTaskStatusEnum.FAILED.getCode()));
            logger.error(serializeBp.obj2Json(mem), ex);
        }
    }
}
