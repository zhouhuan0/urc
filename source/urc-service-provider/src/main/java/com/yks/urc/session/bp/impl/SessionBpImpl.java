package com.yks.urc.session.bp.impl;

import com.alibaba.fastjson.JSONObject;
import com.yks.urc.fw.constant.StringConstant;
import com.yks.urc.motan.MotanSession;
import com.yks.urc.session.bp.api.IRemoveSession;
import com.yks.urc.session.bp.api.ISessionBp;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class SessionBpImpl implements ISessionBp, IRemoveSession {
    @Override
    public String getOperator() {
        return MotanSession.getRequest().getOperator();
    }

    @Override
    public String getStringValue(String strKey) {
        return MotanSession.getRequest().getStringValue(strKey);
    }

    @Override
    public Long getLong(String strKey) {
        return MotanSession.getRequest().getLongValue(strKey);
    }

    @Override
    public void removeCp() {
        MotanSession.removeSession();
    }
}
