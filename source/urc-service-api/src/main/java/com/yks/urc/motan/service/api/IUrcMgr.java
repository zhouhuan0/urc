package com.yks.urc.motan.service.api;

import com.yks.urc.vo.*;

import java.util.List;
import java.util.Map;

public interface IUrcMgr {
    /**
     * 获取指定系统大于某个时间之后有更新的数据权限
     *
     * @param json
     * @return
     */
    ResultVO<List<DataRuleSysVO>> getDataRuleGtDt(String json);

    ResultVO checkSellerId(String jsonStr);
}
