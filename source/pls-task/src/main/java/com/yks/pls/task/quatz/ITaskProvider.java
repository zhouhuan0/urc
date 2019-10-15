package com.yks.pls.task.quatz;

import java.util.List;

public interface ITaskProvider {
    List<PlsEbayTaskDO> getTaskDO();

    void setLastExecuteTime(PlsEbayTaskDO taskDO);

    void setTaskDO(PlsEbayTaskDO jobVO);

    void removeTaskDO(PlsEbayTaskDO jobVO);

    void writeInfoLog(String task_start);

    void writeErrorLog(Exception ex);
}
