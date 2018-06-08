package com.yks.urc.mapper;

import com.yks.urc.entity.DataRule;

public interface DataRuleMapper {
    int deleteByPrimaryKey(Long id);

    int insert(DataRule record);

    int insertSelective(DataRule record);

    DataRule selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DataRule record);

    int updateByPrimaryKey(DataRule record);
}