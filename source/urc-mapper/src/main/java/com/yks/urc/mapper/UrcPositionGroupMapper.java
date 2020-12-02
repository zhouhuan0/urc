package com.yks.urc.mapper;

import com.yks.urc.entity.UrcPositionGroup;

public interface UrcPositionGroupMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UrcPositionGroup record);

    int insertSelective(UrcPositionGroup record);

    UrcPositionGroup selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UrcPositionGroup record);

    int updateByPrimaryKey(UrcPositionGroup record);

    void deleteByGroupId(Long groupId);

}