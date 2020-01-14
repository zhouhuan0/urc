package com.yks.urc.mq.bp.impl;

import com.yks.urc.fw.StringUtility;
import com.yks.urc.lock.bp.impl.RedissonLockBpImpl;
import com.yks.urc.mq.bp.api.IMqCallback;
import com.yks.urc.mq.bp.api.IPubSubBp;
import com.yks.urc.serialize.bp.api.ISerializeBp;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RTopic;
import org.redisson.api.listener.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PubSubBpImpl implements IPubSubBp {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RedissonLockBpImpl redissonLockBp;

    public void sub(String topic, IMqCallback mqCallback) {
        if (StringUtils.isBlank(topic) || mqCallback == null) {
            return;
        }
        RTopic t = redissonLockBp.getRedisson().getTopic(topic);
        t.addListener(new MessageListener<String>() {
            @Override
            public void onMessage(String topic, String msg) {
                logger.info(String.format("%s %s", topic, msg));
                mqCallback.call(topic, msg);
            }
        });
    }

    @Autowired
    private ISerializeBp serializeBp;

    public void pub(String topic, Object msg) {
        if (StringUtils.isBlank(topic) || msg == null) {
            return;
        }
        RTopic t = redissonLockBp.getRedisson().getTopic(topic);
        if (msg instanceof String) {
            t.publish(msg);
        } else {
            t.publish(serializeBp.obj2Json(msg));
        }
    }
}
