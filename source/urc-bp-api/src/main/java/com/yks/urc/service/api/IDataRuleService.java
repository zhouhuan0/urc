package com.yks.urc.service.api;

import java.util.Date;
import java.util.List;

import com.yks.urc.vo.*;

public interface IDataRuleService {

    ResultVO<DataRuleTemplVO> getDataRuleTemplByTemplId(String jsonStr);

    ResultVO<PageResultVO> getDataRuleTempl(String jsonStr);

    ResultVO assignDataRuleTempl2User(String jsonStr);

    ResultVO<DataRuleTemplVO> addOrUpdateDataRuleTempl(String jsonStr);

    /**
     * 获取用户可选择的所有数据授权方案
     *
     * @param userName
     * @return
     */
    ResultVO getMyDataRuleTempl(String pageNumber, String pageData, String userName);

    /**
     * 获取多个用户的所有数据权限
     *
     * @param lstUserName
     * @return
     */
    public ResultVO getDataRuleByUser(List<String> lstUserName,String operator);


    /**
     * Description:删除一个或多个方案
     *
     * @param :
     * @return:
     * @auther: lvcr
     * @date: 2018/6/15 16:41
     * @see
     */
    ResultVO deleteDataRuleTempl(String jsonStr);

    /**
     * Description: 创建或更新多个用户的数据权限
     *
     * @param :
     * @return:
     * @auther: lvcr
     * @date: 2018/6/20 16:10
     * @see
     */
    ResultVO addOrUpdateDataRule(String jsonStr);

    /**
     * Description: 检查给定方案名是否重复
     *
     */
    ResultVO checkDuplicateTemplName(String operator, String newTemplName, String templId);

    /**
     * 获取指定系统大于某个时间之后有更新的数据权限
     * @param sysKey
     * @param dt
     * @param pageSize
     * @return
     */
    ResultVO<List<DataRuleSysVO>> getDataRuleGtDt(String sysKey, Date dt, Integer pageSize);
}
