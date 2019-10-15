package com.yks.urc.permitStat.bp.api;

import java.util.List;

public interface IPermitInverseQueryBp {

    void updatePermitItemInfo(List<String> lstSysKey);

    void doTaskSub(List<String> lstUser);
}
