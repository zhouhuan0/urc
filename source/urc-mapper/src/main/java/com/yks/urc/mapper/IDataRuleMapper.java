package com.yks.urc.mapper;

import com.yks.urc.entity.DataRuleDO;

import java.util.List;

public interface IDataRuleMapper {

    Integer insertBatch(List<DataRuleDO> dataRuleDOList);

}