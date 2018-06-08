package com.yks.urc.mapper;

import com.yks.urc.entity.DataRuleSys;

public interface DataRuleSysMapper {
    int deleteByPrimaryKey(Long id);

    int insert(DataRuleSys record);

    int insertSelective(DataRuleSys record);

    DataRuleSys selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DataRuleSys record);

    int updateByPrimaryKey(DataRuleSys record);
}