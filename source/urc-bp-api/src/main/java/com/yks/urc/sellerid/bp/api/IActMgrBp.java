package com.yks.urc.sellerid.bp.api;


import com.yks.urc.vo.*;

import java.util.List;

public interface IActMgrBp {
    void doAccountSyncTask(String param) throws Exception;

    void saveAct(List<UserInfo4Third> lstAct);

    void syncAct(TaskParamVO taskParamVO, String dtModifyStart, String dtModifyEnd) throws Exception;

    void mergeAct(List<DataRuleSysVO> lstDr);

    void mergeAct(DataRuleSysVO sysDO);
}
