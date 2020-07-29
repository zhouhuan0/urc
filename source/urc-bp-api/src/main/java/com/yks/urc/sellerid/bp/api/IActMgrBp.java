package com.yks.urc.sellerid.bp.api;


import com.yks.urc.vo.AccountInfo4Third;
import com.yks.urc.vo.DataRuleSysVO;
import com.yks.urc.vo.OmsPlatformVO;
import com.yks.urc.vo.UserInfo4Third;

import java.util.List;

public interface IActMgrBp {
    void doAccountSyncTask(String param) throws Exception;

    void saveAct(List<UserInfo4Third> lstAct);

    void syncAct(String dtModifyStart, String dtModifyEnd) throws Exception;

    void mergeAct(List<DataRuleSysVO> lstDr);

    void mergeAct(DataRuleSysVO sysDO);
}
