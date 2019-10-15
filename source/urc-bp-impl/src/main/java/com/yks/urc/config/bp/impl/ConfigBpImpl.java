package com.yks.urc.config.bp.impl;

import com.yks.urc.config.bp.api.IConfigBp;
import com.yks.urc.entity.YksPropSetting;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.mapper.IYksPropSettingMapper;
import com.yks.urc.mq.bp.api.IMqBp;
import com.yks.urc.session.bp.api.ISessionBp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConfigBpImpl implements IConfigBp,InitializingBean {

    @Autowired
    private IYksPropSettingMapper omsPropSettingMapper;

    private Map<String, String> map = new ConcurrentHashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigBpImpl.class);

    @Override
    public void afterPropertiesSet() throws Exception{
        initMap();
        // 订单配置更新 TOPIC
//        mqBp.subscribe(TOPIC_CONFIG_UPDATED, new IMqCallback() {
//            @Override
//            public void call(String topic, String msg) {
//                LOGGER.info("received:{} {} {}", StringUtility.getDateTimeNow_yyyyMMddHHmmssSSS(), topic, msg);
//                initMap();
//            }
//        });
    }

    private void initMap(){
        Map<String, String> map1 = new ConcurrentHashMap<>();
        List<YksPropSetting> omsPropSettings = omsPropSettingMapper.selectOmsPropSettingObjList(null);
        for (YksPropSetting o : omsPropSettings){
            if(o.getPropKey() == null || o.getPropValue() == null){
                continue;
            }
            map1.put(o.getPropKey(),o.getPropValue());
        }
        if (map != null){
            map.clear();
        }
        map= map1;
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

    @Autowired
    private IMqBp mqBp;

    private String TOPIC_CONFIG_UPDATED = "TOPIC_CONFIG_UPDATED";

    @Override
    public void publishConfigUpdate() {
//        mqBp.pubish(TOPIC_CONFIG_UPDATED, String.format("%s %s", TOPIC_CONFIG_UPDATED, StringUtility.getDateTimeNow_yyyyMMddHHmmssSSS()),
//                new IMessageCallBack() {
//                    @Override
//                    public void onCompletion(Exception e) {
//                        if(e!=null){
//                            LOGGER.error("publishConfigUpdate ERROR", e);
//                        }
//                    }
//                });
    }

    @Override
    public String getStringFromDb(String key) {
        YksPropSetting rslt = omsPropSettingMapper.selectOmsProSetingByProoKey(key);
        if (rslt != null) {
            return rslt.getPropValue();
        }
        return StringUtility.Empty;
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
}