package com.yks.urc.mapper;

import com.yks.urc.entity.DataRuleSysDO;
import org.apache.ibatis.annotations.Param;

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
    List<DataRuleSysDO> listByDataRuleId(@Param("dataRuleId") Long dataRuleId);

    int deleteByPrimaryKey(Long id);

    int insert(DataRuleSysDO record);

}