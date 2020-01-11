package com.yks.urc.session.bp.impl;

import com.yks.urc.fw.BeanProvider;
import com.yks.urc.session.bp.api.IRemoveSession;
import com.yks.urc.session.bp.api.ISessionClear;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;

@Component
public class SessionClearImpl implements ISessionClear {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void removeThreadLocal() {
        try {
            Map<String, IRemoveSession> map = BeanProvider.getBeansOfType(IRemoveSession.class);
            Iterator<Map.Entry<String, IRemoveSession>> it = map.entrySet().iterator();
            while (it.hasNext()) {
                IRemoveSession rslt = it.next().getValue();
                try {
                    rslt.removeCp();
                } catch (Exception ex) {
                    log.error("removeThreadLocal", ex);
                }
            }
        } catch (Exception ex) {
            log.error("removeThreadLocal", ex);
        }
    }
}
