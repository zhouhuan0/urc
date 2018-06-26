package com.yks.urc.motan;

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
}
