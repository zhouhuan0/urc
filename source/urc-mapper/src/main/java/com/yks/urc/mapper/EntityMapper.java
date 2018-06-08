package com.yks.urc.mapper;

import com.yks.urc.entity.Entity;

public interface EntityMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Entity record);

    int insertSelective(Entity record);

    Entity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Entity record);

    int updateByPrimaryKey(Entity record);
}