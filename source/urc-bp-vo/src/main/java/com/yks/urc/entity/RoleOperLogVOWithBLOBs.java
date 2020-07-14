package com.yks.urc.entity;

public class RoleOperLogVOWithBLOBs extends RoleOperLogVO {
    private String reqBody;

    private String respBody;

    private String stackTrace;

    public String getReqBody() {
        return reqBody;
    }

    public void setReqBody(String reqBody) {
        this.reqBody = reqBody == null ? null : reqBody.trim();
    }

    public String getRespBody() {
        return respBody;
    }

    public void setRespBody(String respBody) {
        this.respBody = respBody == null ? null : respBody.trim();
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace == null ? null : stackTrace.trim();
    }
}