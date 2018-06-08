package com.yks.urc.mapper;

import com.yks.urc.entity.Field;

public interface FieldMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Field record);

    int insertSelective(Field record);

    Field selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Field record);

    int updateByPrimaryKey(Field record);
}