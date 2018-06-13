package com.yks.urc.service.api;

import com.yks.urc.vo.DataRuleTemplVO;
import com.yks.urc.vo.PageResultVO;
import com.yks.urc.vo.ResultVO;

public interface IDataRuleService {

    ResultVO<DataRuleTemplVO> getDataRuleTemplByTemplId(String jsonStr);

    ResultVO<PageResultVO> getDataRuleTempl(String jsonStr);

    ResultVO assignDataRuleTempl2User(String jsonStr);
}
