package com.yks.urc.config.bp.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yks.urc.config.bp.api.IConfigBp;
import com.yks.urc.entity.YksPropSetting;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.mapper.IYksPropSettingMapper;
import com.yks.urc.mq.bp.api.IMqBp;
import com.yks.urc.mq.bp.api.IMqCallback;
import com.yks.urc.mq.bp.api.IPubSubBp;
import com.yks.urc.serialize.bp.api.ISerializeBp;
import com.yks.urc.session.bp.api.ISessionBp;
import com.yks.urc.vo.RequestVO;
import com.yks.urc.vo.ResultVO;
import com.yks.urc.vo.helper.VoHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConfigBpImpl implements IConfigBp, InitializingBean, ApplicationListener {

    @Autowired
    private IYksPropSettingMapper omsPropSettingMapper;

    private Map<String, String> map = new ConcurrentHashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigBpImpl.class);

    @Autowired
    private IPubSubBp pubSubBp;

    @Override
    public void afterPropertiesSet() throws Exception {
        initMap();
    }

    private void myInit() {

        // 订单配置更新 TOPIC
        pubSubBp.sub(TOPIC_CONFIG_UPDATED, new IMqCallback() {
            @Override
            public void call(String topic, String msg) {
                initMap();
            }
        });
    }

    private void initMap() {
        Map<String, String> map1 = new ConcurrentHashMap<>();
        List<YksPropSetting> omsPropSettings = omsPropSettingMapper.selectOmsPropSettingObjList(null);
        for (YksPropSetting o : omsPropSettings) {
            if (o.getPropKey() == null || o.getPropValue() == null) {
                continue;
            }
            map1.put(o.getPropKey(), o.getPropValue());
        }
        if (map != null) {
            map.clear();
        }
        map = map1;
    }

    @Override
    public String getString(String key) {
        return map.get(key);
    }

    @Override
    public String getString(String key, String defaultValue) {
        if (StringUtility.isNullOrEmpty(getString(key))) {
            return defaultValue;
        }
        return getString(key);
    }

    @Override
    public Boolean getBoolean(String key) {
        return StringUtility.convertToBoolean(getString(key), false);
    }

    @Override
    public String getEnv() {
        return getString("env");
    }

    @Override
    public String get(String key) {
        return getString(key);
    }

    @Override
    public Long getLong(String key) {
        return StringUtility.convertToLong(getString(key), null);
    }

    private String TOPIC_CONFIG_UPDATED = "TOPIC_CONFIG_UPDATED";

    @Override
    public void publishConfigUpdate() {
        pubSubBp.pub(TOPIC_CONFIG_UPDATED, String.format("%s %s", TOPIC_CONFIG_UPDATED, StringUtility.getDateTimeNow_yyyyMMddHHmmssSSS()));
    }

    @Override
    public String getStringFromDb(String key) {
        YksPropSetting rslt = omsPropSettingMapper.selectOmsProSetingByProoKey(key);
        if (rslt != null) {
            return rslt.getPropValue();
        }
        return StringUtility.Empty;
    }

    @Override
    public String getStringFromDb(String key, String defaultValue) {
        String rslt = getStringFromDb(key);
        if (StringUtils.isBlank(rslt)) {
            return defaultValue;
        }
        return rslt;
    }

    @Autowired
    private ISessionBp sessionBp;

    @Override
    public void update2Db(String key, String value) {
        YksPropSetting omsPropSetting = new YksPropSetting();
        omsPropSetting.setPropKey(key);
        omsPropSetting.setPropValue(value);
        omsPropSetting.setCreateBy(sessionBp.getOperator());
        omsPropSetting.setModifiedBy(sessionBp.getOperator());
        omsPropSetting.setCreateTime(new Date());
        omsPropSetting.setModifiedTime(omsPropSetting.getCreateTime());
        omsPropSettingMapper.insertOrUpdateOmsProSeting(omsPropSetting);
    }

    @Autowired
    private ISerializeBp serializeBp;

    @Override
    public ResultVO updateConfig(String json) {
        RequestVO<YksPropSetting> req = serializeBp.json2ObjNew(json, new TypeReference<RequestVO<YksPropSetting>>() {
        });
        this.update2Db(req.data.getPropKey(), req.data.getPropValue());
        this.publishConfigUpdate();
        return VoHelper.getSuccessResult("更新&&发消息成功啦");
    }

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof ContextRefreshedEvent) {
            myInit();
        }
    }
}