package com.yks.urc.sellerid.bp.api;


import com.yks.urc.vo.*;

import java.util.List;

public interface IActMgrBp {
    ISysDataruleContext getSysDataruleContext(String sysKey);

    /**
     * 老账号管理系统平台编码转新账号管理系统平台编码
     *
     * @return
     * @Author panyun@youkeshu.com
     * @Date 2020-08-01 16:19
     */
    String getNewPlatCode(String oldPlatCode);

    /**
     * 获取切换到新账号管理系统获取账号数据权限的平台二位编码
     *
     * @return
     * @Author panyun@youkeshu.com
     * @Date 2020-08-01 16:07
     */
    List<String> getPlatCode();

    void doAccountSyncTask(String param) throws Exception;

    void saveAct(List<UserInfo4Third> lstAct);

    void syncAct(TaskParamVO taskParamVO, String dtModifyStart, String dtModifyEnd) throws Exception;

    void mergeAct(List<DataRuleSysVO> lstDr);

    void mergeAct(DataRuleSysVO sysDO);
}
