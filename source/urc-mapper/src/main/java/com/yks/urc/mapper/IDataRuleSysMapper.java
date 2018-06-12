package com.yks.urc.mapper;

import com.yks.urc.entity.DataRuleSysDO;

import java.util.List;

public interface IDataRuleSysMapper {
    /**
     * Description:
     *
     * @param : dataRuleId
     * @return: List<DataRuleSysDO>
     * @auther: lvcr
     * @date: 2018/6/12 14:47
     * @see
     */
    List<DataRuleSysDO> listByDataRuleId(Long dataRuleId);

    int deleteByPrimaryKey(Long id);

    int insert(DataRuleSysDO record);

}