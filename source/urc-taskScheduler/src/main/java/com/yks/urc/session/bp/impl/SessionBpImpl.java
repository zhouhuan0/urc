package com.yks.urc.session.bp.impl;

import com.yks.urc.motan.MotanSession;
import com.yks.urc.session.bp.api.ISessionBp;
import org.springframework.stereotype.Component;

@Component
public class SessionBpImpl implements ISessionBp {
    @Override
    public String getOperator() {
        return "taskScheduler";
    }

    @Override
    public String getStringValue(String strKey) {
        return "";
    }

    @Override
    public Long getLong(String strKey) {
        return 0L;
    }
}
