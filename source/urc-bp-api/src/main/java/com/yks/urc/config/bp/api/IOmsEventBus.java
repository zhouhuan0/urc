package com.yks.urc.config.bp.api;

public interface IOmsEventBus {
    void addListener(String topic, IConfigChanged callback);

    void publishTopic(String topic);
}
