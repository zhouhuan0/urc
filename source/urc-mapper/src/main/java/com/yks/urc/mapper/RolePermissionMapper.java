package com.yks.urc.mapper;

import com.yks.urc.entity.RolePermissionDO;

public interface RolePermissionMapper {
    int deleteByPrimaryKey(String RolePermissionDOId);

    int insert(RolePermissionDO record);

    int insertSelective(RolePermissionDO record);

    RolePermissionDO selectByPrimaryKey(String RolePermissionDOId);

    int updateByPrimaryKeySelective(RolePermissionDO record);

    int updateByPrimaryKeyWithBLOBs(RolePermissionDO record);

    int updateByPrimaryKey(RolePermissionDO record);
}