package com.yks.urc.mapper;

import com.yks.urc.entity.RoleOperLogVO;
import com.yks.urc.entity.RoleOperLogVOWithBLOBs;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IRoleOperLogMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RoleOperLogVOWithBLOBs record);

    int insertSelective(RoleOperLogVOWithBLOBs record);

    RoleOperLogVOWithBLOBs selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RoleOperLogVOWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(RoleOperLogVOWithBLOBs record);

    int updateByPrimaryKey(RoleOperLogVO record);

    int insertBatch(@Param("lst") List<RoleOperLogVOWithBLOBs> lstToAdd);
}