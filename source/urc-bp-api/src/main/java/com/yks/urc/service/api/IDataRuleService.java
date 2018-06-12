package com.yks.urc.service.api;

import com.yks.urc.vo.DataRuleTemplVO;
import com.yks.urc.vo.PageResultVO;
import com.yks.urc.vo.ResultVO;

public interface IDataRuleService {

    ResultVO<DataRuleTemplVO> getDataRuleTemplByTemplId(String templId);

    ResultVO<PageResultVO> getDataRuleTempl(String jsonStr);
}
