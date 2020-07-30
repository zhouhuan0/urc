package com.yks.actmgr.motan.service.api;

import com.yks.urc.vo.BasePlatformInfo;
import com.yks.urc.vo.Response4GetAccountInfo;
import com.yks.urc.vo.Response4GetUserAccountInfo;
import com.yks.urc.vo.ResultVO;

import java.util.List;

public interface IActMgrService {
    /**
     * 同步账号管理系统账号
     *
     * @return
     * @Author panyun@youkeshu.com
     * @Date 2020-07-22 20:48
     */
    ResultVO<Response4GetAccountInfo> getAccountInfo(String param);

    ResultVO<Response4GetUserAccountInfo> getUserAccountInfo(String param);

    ResultVO<List<BasePlatformInfo>> getPlatformCode(String param);

}
