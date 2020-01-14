package com.yks.urc.motan;

import com.alibaba.fastjson.JSONObject;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.fw.constant.StringConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import java.util.Map;

public class MotanSession {
    private static final ThreadLocal<MotanRequest> curOperator = new ThreadLocal<MotanRequest>();

    public static void setRequest(MotanRequest operator) {
        curOperator.set(operator);
    }

    public static MotanRequest getRequest() {
        MotanRequest req = curOperator.get();
        if (req == null) return MotanRequest.Empty;
        return req;
    }

    public static void initialSession(Object arg) {
        if (arg == null) return;
        if (arg instanceof Map) {
            Map<String, String> mapArg = (Map<String, String>) arg;
            MotanRequest req = new MotanRequest();
            req.setMapArg(mapArg);
            MotanSession.setRequest(req);
            if (StringUtils.isNotBlank(getRequest().getRequestId())) {
                MDC.put(StringConstant.requestid, getRequest().getRequestId());
            }
        } else if (arg instanceof String) {
            JSONObject jo = StringUtility.parseString((String) arg);
            MotanRequest req = new MotanRequest();
            req.setJSONObjectArg(jo);
            MotanSession.setRequest(req);
            if (StringUtils.isNotBlank(getRequest().getRequestId())) {
                MDC.put(StringConstant.requestid, getRequest().getRequestId());
            }
        }
    }

    public static void removeSession() {
        curOperator.remove();
        MDC.remove(StringConstant.requestid);
    }
}
