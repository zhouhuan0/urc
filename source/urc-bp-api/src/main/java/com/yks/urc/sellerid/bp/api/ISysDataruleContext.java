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

    String getLastPointKey();

    /**
     * 获取要pull的从新账号管理系统roleId,空表示所有
     *
     * @return
     * @Author panyun@youkeshu.com
     * @Date 2020-08-04 16:30
     */
    List<Integer> getRoleIds();

    /**
     * 获取要更新最后修改时间的sysKey,pull新账号管理系统后调用;是否发MQ,在发MQ最后一环节，由 datarule.notSendMq.sysKey 配置决定
     *
     * @return
     * @Author panyun@youkeshu.com
     * @Date 2020-08-04 16:33
     */
    List<String> getSendMqSysKey();
}
