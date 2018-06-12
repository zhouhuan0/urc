package com.yks.urc.mapper;

import com.yks.urc.entity.DataRuleTemplDO;

public interface IDataRuleTemplMapper {

    int insert(DataRuleTemplDO record);

    DataRuleTemplDO selectByTemplId(Long id);


}