package com.yks.urc.mapper;


import com.yks.urc.entity.YksTaskVO;

import java.util.List;

public interface IYksTaskMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YksTaskVO record);

    int insertSelective(YksTaskVO record);

    YksTaskVO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(YksTaskVO record);

    int updateByPrimaryKey(YksTaskVO record);

    List<YksTaskVO> getByTaskGroup(String taskGroup);

    int setLastExecuteTime(Long taskId);
    
    YksTaskVO selectByTaskName(String taskName);

	int disableTaskByTaskName(String taskName);

	List<YksTaskVO> selectAllEnableTask();
}