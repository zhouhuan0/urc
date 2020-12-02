package com.yks.urc.mapper;

import com.yks.urc.entity.UrcGroupPermission;

public interface UrcGroupPermissionMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UrcGroupPermission record);

    int insertSelective(UrcGroupPermission record);

    UrcGroupPermission selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UrcGroupPermission record);

    int updateByPrimaryKeyWithBLOBs(UrcGroupPermission record);

    int updateByPrimaryKey(UrcGroupPermission record);

    int deleteByGroupId(Long groupId);
}