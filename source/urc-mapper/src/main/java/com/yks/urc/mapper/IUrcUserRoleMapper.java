package com.yks.urc.mapper;

import com.yks.urc.entity.UrcUserRoleVO;

public interface IUrcUserRoleMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UrcUserRoleVO record);

    int insertSelective(UrcUserRoleVO record);

    UrcUserRoleVO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UrcUserRoleVO record);

    int updateByPrimaryKey(UrcUserRoleVO record);
}