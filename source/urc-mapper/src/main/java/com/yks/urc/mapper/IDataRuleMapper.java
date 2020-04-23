package com.yks.urc.mapper;

import com.yks.urc.entity.DataRuleDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IDataRuleMapper {
    /**
     * Description: 批量添加数据
     *
     * @param :
     * @return:
     * @auther: lvcr
     * @date: 2018/6/15 14:16
     * @see
     */
    Integer insertBatch(List<DataRuleDO> dataRuleDOList);

    /**
     * Description: 根据userName列表批量删除
     *
     * @param :
     * @return:
     * @auther: lvcr
     * @date: 2018/6/15 14:16
     * @see
     */
    Integer delBatchByUserNames(List<String> lstUserName);

    /**
     * Description: 根据userName获取dataRuleIds列表
     *
     * @param :
     * @return:
     * @auther: lvcr
     * @date: 2018/6/29 15:35
     * @see
     */
    List<Long> getDataRuleIdsByUserName(@Param("userNames") List<String> userNames);

    /**
     * 得到DataRule
     * @param dataRuleDO
     * @return
     */
    DataRuleDO getDataRule(DataRuleDO dataRuleDO);

    /**
     * 根据用户名查找存在的用户名
     * @return
     * @Author panyun@youkeshu.com
     * @Date 2020-04-23 17:37
     */
    List<String> getExistsByUserName(@Param("lstUserName") List<String> lstUserName);

    List<DataRuleDO> getDataRuleByUserName(@Param("lstUserName") List<String> lstUserName);
}