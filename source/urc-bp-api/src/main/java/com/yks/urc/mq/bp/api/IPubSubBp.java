package com.yks.urc.mq.bp.api;

public interface IPubSubBp {
    void sub(String topic, IMqCallback mqCallback);

    void pub(String topic, Object msg);
}
