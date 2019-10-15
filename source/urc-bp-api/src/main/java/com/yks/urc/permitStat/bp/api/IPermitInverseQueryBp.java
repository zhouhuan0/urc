package com.yks.urc.permitStat.bp.api;

import com.yks.urc.vo.ResultVO;

import java.util.List;

public interface IPermitInverseQueryBp {

    void updatePermitItemInfo(List<String> lstSysKey);

    void doTaskSub(List<String> lstUser);

    ResultVO getUserListByPermitKey(String json);

    ResultVO exportUserListByPermitKey(String json);
}
