package com.yks.urc.mapper;

import com.yks.urc.entity.DataRuleTemplDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface IDataRuleTemplMapper {

    /**
     * Description: 新增
     *
     * @param :
     * @return:
     * @auther: lvcr
     * @date: 2018/6/13 15:46
     * @see
     */
    int insert(DataRuleTemplDO record);

    /**
     * Description: 根据tempIld获取数据权限模板 包括对应的数据权限Sys  行权限 列权限
     *
     * @param :
     * @return:
     * @auther: lvcr
     * @date: 2018/6/15 17:12
     * @see
     */
    DataRuleTemplDO selectByTemplId(@Param("templId") Long id, @Param("createBy") String createBy);

    /**
     * Description: 根据条件分页获取 dataRuleTempDOs List数据
     *
     * @param :
     * @return:
     * @auther: lvcr
     * @date: 2018/6/12 20:18
     * @see
     */
    List<DataRuleTemplDO> listDataRuleTemplDOsByPage(Map<String, Object> data);

    /**
     * Description: 获取总数
     *
     * @param :
     * @return:
     * @auther: lvcr
     * @date: 2018/6/13 11:26
     * @see
     */
    Long getCounts(@Param("createBy") String createBy);

    /**
     * Description: 根据templId删除记录
     *
     * @param : templId
     * @return: Integer
     * @auther: lvcr
     * @date: 2018/6/14 17:26
     * @see
     */
    Integer deleteByTemplId(@Param("templId") Long templId);

    /**
     * Description: 根据templId删除 权限模板数据、权限数据Sys、行权限、列权限
     *
     * @param : templId
     * @return: Integer
     * @auther: lvcr
     * @date: 2018/6/14 17:26
     * @see
     */
    Integer delTemplDatasById(@Param("templId") Long templId);

    /**
     * 获取用户可选择的所有数据授权方案
     *
     * @param userName
     * @return
     */
    List<DataRuleTemplDO> getMyDataRuleTempl(String userName);


    /**
     * Description: 根据templId删除 权限模板数据、权限数据Sys、行权限、列权限
     *
     * @param : templId
     * @return: Integer
     * @auther: lvcr
     * @date: 2018/6/14 17:26
     * @see
     */
    Integer delTemplDatasByIds(List<Long> templId);


}