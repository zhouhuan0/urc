package com.yks.urc.session.bp.impl;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.yks.urc.fw.IpUtil;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.fw.constant.StringConstant;
import com.yks.urc.motan.MotanSession;
import com.yks.urc.serialize.bp.api.ISerializeBp;
import com.yks.urc.session.bp.api.IRemoveSession;
import com.yks.urc.session.bp.api.ISessionBp;
import com.yks.urc.vo.RequestVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SessionBpImpl implements ISessionBp, IRemoveSession {
    protected ThreadLocal<String> requestId = new ThreadLocal<String>();

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

    @Autowired
    private ISerializeBp serializeBp;

    @Override
    public String cpString() {
        RequestVO requestVO = new RequestVO();
        requestVO.requestId = getRequestId();
        requestVO.operator = getOperator();
        return serializeBp.obj2Json(requestVO);
    }

    @Override
    public void initCp(String strCp) {
        removeCp();
        RequestVO req = serializeBp.json2ObjNew(strCp, new TypeReference<RequestVO>() {
        });
        if(req!=null) {
            if (StringUtils.isNotBlank(req.requestId)) {
                requestId.set(req.requestId);
                MDC.put(StringConstant.requestid, req.requestId);
            }
        }
    }

    @Override
    public String getIp() {
        return IpUtil.getAddressIp();
    }

    public String getRequestId() {
        // 先取线程变量
        if (!StringUtility.isNullOrEmpty(requestId.get())) {
            return requestId.get();
        }
        // 再取各自的实现
        return getMyRequestId();
    }

    private String getMyRequestId() {
        String rslt = MotanSession.getRequest().getRequestId();
        if (!StringUtility.isNullOrEmpty(rslt)) return rslt;
        return StringUtils.EMPTY;
    }

    @Override
    public void removeCp() {
        requestId.remove();
        MDC.remove(StringConstant.requestid);
        MotanSession.removeSession();
    }

}
