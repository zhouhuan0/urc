package com.yks.urc.motan.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.fw.constant.StringConstant;
import com.yks.urc.log.Log;
import com.yks.urc.motan.MotanSession;
import com.yks.urc.motan.service.api.IUrcMgr;
import com.yks.urc.sellerid.bp.api.ISellerIdBp;
import com.yks.urc.service.api.IDataRuleService;
import com.yks.urc.service.api.IPermissionService;
import com.yks.urc.vo.DataRuleSysVO;
import com.yks.urc.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

public class UrcMgrImpl implements IUrcMgr {

    @Autowired
    private IDataRuleService dataRuleService;

    @Autowired
    IPermissionService permissionService;


    @Override
    @Log("获取指定系统的数据权限")
    public ResultVO<List<DataRuleSysVO>> getDataRuleGtDt(String json) {
        JSONObject jObj = MotanSession.getRequest().getJSONObjectArg();
        String sysKey = jObj.getString(StringConstant.sysKey);
        Date dt = StringUtility.convertToDate(jObj.getString("dt"), null);
        Integer pageSize = jObj.getInteger("pageSize");
        return dataRuleService.getDataRuleGtDt(sysKey, dt, pageSize);
    }


    @Autowired
    private ISellerIdBp sellerIdBp;

    @Log("匹配正确的销售账号")
    @Override
    public ResultVO checkSellerId(String jsonStr) {
        return sellerIdBp.checkSellerId(jsonStr);
    }

    @Override
    @Log("快速分配数据权限模板给用户")
    public ResultVO assignDataRuleTempl2User(String jsonStr) throws Exception {
        return dataRuleService.assignDataRuleTempl2User(jsonStr);
    }

    @Override
    @Log("推送系统功能及子功能权限")
    public ResultVO importSysPermit(String jsonStr) {
        return permissionService.importSysPermit(jsonStr);
    }
}
