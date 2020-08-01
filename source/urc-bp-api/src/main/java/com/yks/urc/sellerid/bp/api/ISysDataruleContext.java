package com.yks.urc.sellerid.bp.api;

import com.yks.urc.vo.DataRuleSysVO;
import com.yks.urc.vo.PlatformAccount4Third;

import java.util.List;

public interface ISysDataruleContext {
    String getSysKey();

    String getEntityCode();

    String getFieldCode();

    String getQueryEntityCode();

    String getPlatformId(PlatformAccount4Third t);

    List<String> filterActMgrPlatCode(List<String> operValuesArr);

    void handleIfAll(DataRuleSysVO sysVO);
}
