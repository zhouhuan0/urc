package com.yks.urc.permitStat.bp.api;

import java.util.List;

public interface IPermitRefreshTaskBp {
    /**
     * 修改角色时用
     *
     * @return
     * @Author panyun@youkeshu.com
     * @Date 2019-10-15 14:21
     */
    void addPermitRefreshTask(List<String> lstUserName);

    /**
     * 导入系统功能权限时用
     *
     * @return
     * @Author panyun@youkeshu.com
     * @Date 2019-10-15 14:21
     */
    void addPermitRefreshTaskForImportSysPermit(List<String> lstSysKey);
}
