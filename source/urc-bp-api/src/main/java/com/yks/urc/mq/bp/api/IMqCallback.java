package com.yks.urc.mq.bp.api;

public interface IMqCallback {
    void call(String topic, String msg);
}
