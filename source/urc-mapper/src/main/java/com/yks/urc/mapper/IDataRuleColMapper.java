package com.yks.urc.mapper;

import com.yks.urc.entity.DataRuleColDO;
import com.yks.urc.entity.DataRuleDO;

import java.util.List;

public interface IDataRuleColMapper {
    int insert(DataRuleColDO record);

    int insertBatch(List<DataRuleColDO> dataRuleDOList);

    int deleteByPrimaryKey(Long id);

    int insertSelective(DataRuleColDO record);

    DataRuleColDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DataRuleColDO record);

    int updateByPrimaryKeyWithBLOBs(DataRuleColDO record);

    int updateByPrimaryKey(DataRuleColDO record);
    
    
	List<DataRuleColDO> listRuleColBySysKey(String syskeyList);
}