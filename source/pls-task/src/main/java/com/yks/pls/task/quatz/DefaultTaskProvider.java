package com.yks.pls.task.quatz;

import java.util.List;

public class DefaultTaskProvider implements ITaskProvider{
    @Override
    public List<PlsEbayTaskDO> getTaskDO() {
        return null;
    }

    @Override
    public void setLastExecuteTime(PlsEbayTaskDO taskDO) {

    }

    @Override
    public void setTaskDO(PlsEbayTaskDO jobVO) {

    }

    @Override
    public void removeTaskDO(PlsEbayTaskDO jobVO) {

    }

    @Override
    public void writeInfoLog(String task_start) {

    }

    @Override
    public void writeInfoLog(String logger, String msg) {

    }

    @Override
    public void writeErrorLog(Exception ex) {

    }
}
