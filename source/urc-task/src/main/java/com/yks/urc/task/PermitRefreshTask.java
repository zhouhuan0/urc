package com.yks.urc.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component
public class PermitRefreshTask extends BaseTask {
    private static final Logger logger = LoggerFactory.getLogger(PermitRefreshTask.class);

    @Autowired
    private IPermitInverseQueryBp permitInverseQueryBp;

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
        Integer pageSize = 20;
        if(!StringUtility.isNullOrEmpty(param)){
            JSONObject jsonObject = JSON.parseObject(param);
            pageSize = jsonObject.getInteger("size");
        }
        List<PermitRefreshTaskVO> lstToDo = permitRefreshTaskMapper.selectToDo(pageSize);
        if (CollectionUtils.isEmpty(lstToDo)) {
            return;
        }

        //按类型分组
        Map<Integer, List<PermitRefreshTaskVO>> listMap = lstToDo.stream().collect(Collectors.groupingBy(PermitRefreshTaskVO::getTaskType));
        for (Integer integer : listMap.keySet()) {
            List<PermitRefreshTaskVO> permitRefreshTaskVOS = listMap.get(integer);
            if(CollectionUtils.isEmpty(permitRefreshTaskVOS)){
                continue;
            }
            if (StringUtility.stringEqualsIgnoreCaseObj(PermitRefreshTaskTypeEnum.REFRESH_USER.getCode(), integer)) {
                doOne(permitRefreshTaskVOS);
            } else if (StringUtility.stringEqualsIgnoreCaseObj(PermitRefreshTaskTypeEnum.REFRESH_SYS.getCode(), integer)) {
                doTwo(permitRefreshTaskVOS);
            }
        }
    }

    public void doTwo(List<PermitRefreshTaskVO> memList) {
        try {
            List<String> lstSysKey = new ArrayList<>();
            for (PermitRefreshTaskVO permitRefreshTaskVO : memList) {
                if (StringUtils.isBlank(permitRefreshTaskVO.getTaskParam())) {
                    continue;
                }
                List<String> sysKeyList = serializeBp.json2ObjNew(permitRefreshTaskVO.getTaskParam(), new TypeReference<ArrayList<String>>() {
                });
                if (CollectionUtils.isEmpty(sysKeyList)) {
                    continue;
                }
                lstSysKey.addAll(sysKeyList);
            }
            //系统去重
            List<String> list = lstSysKey.stream().distinct().collect(Collectors.toList());
            permitInverseQueryBp.updatePermitItemInfo(list);
            for (PermitRefreshTaskVO permitRefreshTaskVO : memList) {
                permitRefreshTaskVO.setTaskStatus(StringUtility.convertToByte(PermitRefreshTaskStatusEnum.SUCCESS.getCode()));
                permitRefreshTaskMapper.updateTaskStatus(permitRefreshTaskVO);
            }

        } catch (Exception ex) {
            for (PermitRefreshTaskVO permitRefreshTaskVO : memList) {
                permitRefreshTaskVO.setTaskStatus(StringUtility.convertToByte(PermitRefreshTaskStatusEnum.FAILED.getCode()));
                permitRefreshTaskMapper.updateTaskStatus(permitRefreshTaskVO);
            }
            logger.error("doTwo error", ex);
        }
    }

    public void doOne(List<PermitRefreshTaskVO> memList) {
        try {
            List<String> lstUserName = new ArrayList<>();
            for (PermitRefreshTaskVO permitRefreshTaskVO : memList) {
                if (StringUtils.isBlank(permitRefreshTaskVO.getTaskParam())) {
                    continue;
                }
                List<String> userNameList = serializeBp.json2ObjNew(permitRefreshTaskVO.getTaskParam(), new TypeReference<ArrayList<String>>() {
                });
                if (CollectionUtils.isEmpty(userNameList)) {
                   continue;
                }
                lstUserName.addAll(userNameList);
            }
            //账号去重
            List<String> list = lstUserName.stream().distinct().filter(x -> !StringUtility.isNullOrEmpty(x)).collect(Collectors.toList());
            permitInverseQueryBp.doTaskSub(list);
            for (PermitRefreshTaskVO permitRefreshTaskVO : memList) {
                permitRefreshTaskVO.setTaskStatus(StringUtility.convertToByte(PermitRefreshTaskStatusEnum.SUCCESS.getCode()));
                permitRefreshTaskMapper.updateTaskStatus(permitRefreshTaskVO);
            }

        } catch (Exception ex) {
            for (PermitRefreshTaskVO permitRefreshTaskVO : memList) {
                permitRefreshTaskVO.setTaskStatus(StringUtility.convertToByte(PermitRefreshTaskStatusEnum.FAILED.getCode()));
                permitRefreshTaskMapper.updateTaskStatus(permitRefreshTaskVO);
            }
            logger.error("doOne error", ex);
        }
    }
}
