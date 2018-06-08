package com.yks.urc.mapper;

import com.yks.urc.entity.Expression;

public interface ExpressionMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Expression record);

    int insertSelective(Expression record);

    Expression selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Expression record);

    int updateByPrimaryKeyWithBLOBs(Expression record);

    int updateByPrimaryKey(Expression record);
}