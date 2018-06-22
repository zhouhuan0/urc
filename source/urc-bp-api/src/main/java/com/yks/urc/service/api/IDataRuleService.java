package com.yks.urc.service.api;

import java.util.List;

import com.yks.urc.vo.DataRuleTemplVO;
import com.yks.urc.vo.DataRuleVO;
import com.yks.urc.vo.PageResultVO;
import com.yks.urc.vo.ResultVO;

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
    ResultVO getMyDataRuleTempl(int pageNumber, int pageData, String userName);

    /**
     * 获取多个用户的所有数据权限
     *
     * @param lstUserName
     * @return
     */
    public ResultVO getDataRuleByUser(List<String> lstUserName);


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
}
