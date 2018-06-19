package com.yks.urc.mapper;

import com.yks.urc.entity.DataRuleColDO;
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

    /**
     * Description: 新增
     *
     * @param : record
     * @return:
     * @auther: lvcr
     * @date: 2018/6/14 15:50
     * @see
     */
    int insert(DataRuleSysDO record);

    /**
     * Description: 批量新增
     *
     * @param :
     * @return:
     * @auther: lvcr
     * @date: 2018/6/15 17:41
     * @see
     */
    int insertBatch(List<DataRuleSysDO> dataRuleSysDOS);

    /**
     * Description: 根据dataRuleIds 获取数据权限Sys  包括对应的 行权限  列权限
     *
     * @param :
     * @return:
     * @auther: lvcr
     * @date: 2018/6/15 17:43
     * @see
     */
    List<DataRuleSysDO> getDataRuleSysDatas(@Param("dataRuleId") Long dataRuleId);

}