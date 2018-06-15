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
     * @param userName
     * @return
     */
    ResultVO getMyDataRuleTempl(String userName);
    
    /**
     * 获取多个用户的所有数据权限
     * @param lstUserName
     * @return
     */
    public List<DataRuleVO> getDataRuleByUser(List<String> lstUserName);
    
}
