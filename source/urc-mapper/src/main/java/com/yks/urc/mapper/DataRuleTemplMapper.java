package com.yks.urc.mapper;

import com.yks.urc.entity.DataRuleTempl;

public interface DataRuleTemplMapper {
    int deleteByPrimaryKey(Long id);

    int insert(DataRuleTempl record);

    int insertSelective(DataRuleTempl record);

    DataRuleTempl selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DataRuleTempl record);

    int updateByPrimaryKey(DataRuleTempl record);
}