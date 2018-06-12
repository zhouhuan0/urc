package com.yks.urc.mapper;

import com.yks.urc.entity.DataRuleTemplDO;

import java.util.List;
import java.util.Map;

public interface IDataRuleTemplMapper {

    int insert(DataRuleTemplDO record);

    DataRuleTemplDO selectByTemplId(Long id);

    List<DataRuleTemplDO> listDataRuleTemplDOsByPage(Map<String, Object> data);


}