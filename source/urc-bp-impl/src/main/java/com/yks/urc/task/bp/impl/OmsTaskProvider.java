package com.yks.urc.task.bp.impl;

import com.yks.pls.task.quatz.ITaskProvider;
import com.yks.pls.task.quatz.PlsEbayTaskDO;
import com.yks.urc.entity.YksTaskLogVO;
import com.yks.urc.entity.YksTaskVO;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.mapper.IYksTaskLogMapper;
import com.yks.urc.mapper.IYksTaskMapper;
import com.yks.urc.session.bp.api.ISessionBp;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OmsTaskProvider implements ITaskProvider {
    private static final Logger log = LoggerFactory.getLogger(OmsTaskProvider.class);
    @Autowired
    private ISessionBp sessionBp;

    @Autowired
    private IYksTaskMapper taskMapper;

    byte INFO = (byte) 1;
    byte ERROR = (byte) 2;

    @Override
    public List<PlsEbayTaskDO> getTaskDO() {

        String group = sessionBp.getOperator();

        List<YksTaskVO> rsltFromDb = taskMapper.getByTaskGroup(group);

        log.debug("OmsTaskProvider,group={},tasks:{}", group, rsltFromDb);

        List<PlsEbayTaskDO> rslt = new ArrayList<>();
//        rsltFromDb 转 rslt
        if (rsltFromDb == null || rsltFromDb.size() < 1) {
            return null;
        }
        for (YksTaskVO plsEbayTaskDO : rsltFromDb) {
            PlsEbayTaskDO plsEbayTaskDO1 = new PlsEbayTaskDO();
            plsEbayTaskDO1.setTaskId(plsEbayTaskDO.getTaskId());
            plsEbayTaskDO1.setTaskGroup(plsEbayTaskDO.getTaskGroup());
            plsEbayTaskDO1.setTaskName(plsEbayTaskDO.getTaskName());
            plsEbayTaskDO1.setTriggerStartTime(plsEbayTaskDO.getTriggerStartTime());
            plsEbayTaskDO1.setBeanMethod(plsEbayTaskDO.getBeanMethod());
            plsEbayTaskDO1.setBeanMethodParam(plsEbayTaskDO.getBeanMethodParam());
            plsEbayTaskDO1.setBeanName(plsEbayTaskDO.getBeanName());
            plsEbayTaskDO1.setCreatedTime(plsEbayTaskDO.getCreatedTime());
            plsEbayTaskDO1.setCreator(plsEbayTaskDO.getCreator());
            plsEbayTaskDO1.setCronExpression(plsEbayTaskDO.getCronExpression());
            plsEbayTaskDO1.setId(plsEbayTaskDO.getId());
            plsEbayTaskDO1.setIsEnabled(StringUtility.convertToBoolean(plsEbayTaskDO.getIsEnabled(), false));
            plsEbayTaskDO1.setModifiedTime(plsEbayTaskDO.getModifiedTime());
            plsEbayTaskDO1.setModifier(plsEbayTaskDO.getModifier());

            rslt.add(plsEbayTaskDO1);
        }
        return rslt;
    }

    @Override
    public void setLastExecuteTime(PlsEbayTaskDO taskDO) {
        if (taskDO == null || taskDO.getTaskId() == null) {
            return;
        }
        // 根据 taskId，更新trigger_start_time字段为 当前时间
        taskMapper.setLastExecuteTime(taskDO.getTaskId());
    }

    ThreadLocal<PlsEbayTaskDO> curTaskDO = new ThreadLocal<>();

    @Override
    public void setTaskDO(PlsEbayTaskDO jobVO) {
        if (jobVO != null) {
            curTaskDO.set(jobVO);
        }
    }

    private PlsEbayTaskDO getCurrentThreadTask() {
        return curTaskDO.get();
    }

    @Override
    public void removeTaskDO(PlsEbayTaskDO jobVO) {
        curTaskDO.remove();
    }

    @Override
    public void writeInfoLog(String msg) {
        try {
            if (getCurrentThreadTask() == null) {
                return;
            }
            setLastExecuteTime(getCurrentThreadTask());
            writeLog(log.getName(), INFO, msg);
        } catch (Exception ex) {
            log.error(msg, ex);
        }
    }


    @Override
    public void writeInfoLog(String logger, String msg) {
        try {
            writeLog(logger, INFO, msg);
        } catch (Exception ex) {
            log.error(msg, ex);
        }
    }

    @Override
    public void writeErrorLog(Exception ex) {
        try {
            writeLog(log.getName(), ERROR, ExceptionUtils.getStackTrace(ex));
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(ex), e);
        }
    }


    private Long getTaskId() {
        if (getCurrentThreadTask() == null) {
            return 0L;
        }
        return getCurrentThreadTask().getTaskId();
    }

    @Autowired
    private IYksTaskLogMapper yksTaskLogMapper;

    private void writeLog(String logger, Byte logLevel, String msg) {
        Long taskId = getTaskId();
        YksTaskLogVO logVO = new YksTaskLogVO();
        logVO.setTaskId(taskId);
        logVO.setLogger(logger);
        logVO.setLogLevel(logLevel);
        logVO.setMsg(msg);
        logVO.setCreator(sessionBp.getOperator());
        logVO.setCreatorIp(sessionBp.getIp());
        yksTaskLogMapper.insertSelective(logVO);
    }
}
