package com.yks.urc.sellerid.bp.impl;

import com.yks.urc.fw.constant.StringConstant;
import com.yks.urc.sellerid.bp.api.ISysDataruleContext;
import com.yks.urc.vo.PlatformAccount4Third;
import org.springframework.stereotype.Component;

@Component
public class SysDataruleContextImplPls implements ISysDataruleContext {
    @Override
    public String getSysKey() {
        return "008";
    }


    @Override
    public String getEntityCode() {
        return StringConstant.E_PlsShopAccount;
    }

    @Override
    public String getFieldCode() {
        return StringConstant.F_PlsShopAccount;
    }

    @Override
    public String getQueryEntityCode() {
        return StringConstant.E_PlatformShopSite;
    }

    @Override
    public String getPlatformId(PlatformAccount4Third t) {
        return t.getPlatformCodeOld();
    }
}
