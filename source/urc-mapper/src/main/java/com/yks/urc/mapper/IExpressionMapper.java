package com.yks.urc.mapper;

import com.yks.urc.entity.ExpressionDO;

import java.util.List;

public interface IExpressionMapper {

    List<ExpressionDO> listExpressionDOs(Long[] sqlIds);

    int deleteByPrimaryKey(Long id);

    int insert(ExpressionDO record);

    int insertSelective(ExpressionDO record);

}