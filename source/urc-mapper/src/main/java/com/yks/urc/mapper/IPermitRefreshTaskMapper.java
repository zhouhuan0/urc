package com.yks.urc.mapper;

import com.yks.urc.entity.PermitRefreshTaskVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IPermitRefreshTaskMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PermitRefreshTaskVO record);

    int insertSelective(PermitRefreshTaskVO record);

    PermitRefreshTaskVO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PermitRefreshTaskVO record);

    int updateByPrimaryKeyWithBLOBs(PermitRefreshTaskVO record);

    int updateByPrimaryKey(PermitRefreshTaskVO record);

    List<PermitRefreshTaskVO> selectToDo(@Param("pageSize") Integer pageSize);

    void updateTaskStatus(PermitRefreshTaskVO mem);
}