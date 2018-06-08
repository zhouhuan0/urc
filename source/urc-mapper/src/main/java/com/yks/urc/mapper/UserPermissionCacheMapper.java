package com.yks.urc.mapper;

import com.yks.urc.entity.UserPermissionCache;

public interface UserPermissionCacheMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserPermissionCache record);

    int insertSelective(UserPermissionCache record);

    UserPermissionCache selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserPermissionCache record);

    int updateByPrimaryKeyWithBLOBs(UserPermissionCache record);

    int updateByPrimaryKey(UserPermissionCache record);
}