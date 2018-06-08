package com.yks.urc.mapper;

import com.yks.urc.entity.UrcSql;

public interface UrcSqlMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UrcSql record);

    int insertSelective(UrcSql record);

    UrcSql selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UrcSql record);

    int updateByPrimaryKey(UrcSql record);
}