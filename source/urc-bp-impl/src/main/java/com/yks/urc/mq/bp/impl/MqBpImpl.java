package com.yks.urc.mq.bp.impl;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yks.mq.utils.KafkaProducerSingleton;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.mq.bp.api.IMqBp;
import com.yks.urc.vo.DataRuleSysVO;
import com.yks.urc.vo.DataRuleVO;

import java.util.List;

@Component
public class MqBpImpl implements IMqBp {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private String getDataRuleTopic(String sysKey) {
        return String.format("URC_USER_DATARULE_%s", sysKey);
    }

    @Override
    public void send2Mq(DataRuleVO dr) {
        if (dr == null || dr.lstDataRuleSys == null || dr.lstDataRuleSys.size() == 0)
            return;
        for (DataRuleSysVO drSys : dr.lstDataRuleSys) {
            if (StringUtility.isNullOrEmpty(drSys.sysKey))
                continue;
            String sysKey = drSys.sysKey;
            String topic = getDataRuleTopic(sysKey);
            String value = StringUtility.toJSONString_NoException(drSys);
            ProducerRecord<String, String> arg0 = new ProducerRecord<String, String>(topic, value);

            Callback arg1 = new Callback() {
                @Override
                public void onCompletion(RecordMetadata arg0, Exception arg1) {

                }
            };
            KafkaProducerSingleton.getInstance(null).send(arg0, arg1);
        }
    }

    @Override
    public void send2Mq(List<DataRuleVO> dataRuleVOList) {
        if (dataRuleVOList == null || dataRuleVOList.isEmpty()) {
            return;
        }
        for (DataRuleVO dataRuleVO : dataRuleVOList) {
            List<DataRuleSysVO> dataRuleSysVOS = dataRuleVO.lstDataRuleSys;
            for (DataRuleSysVO drSys : dataRuleSysVOS) {
                if (StringUtility.isNullOrEmpty(drSys.sysKey)) {
                    continue;
                }
                if(StringUtility.isNullOrEmpty(drSys.getUserName())){
                    drSys.setUserName(dataRuleVO.getUserName());
                }
                String sysKey = drSys.sysKey;
                String topic = getDataRuleTopic(sysKey);
                String value = StringUtility.toJSONString_NoException(drSys);
                logger.info("数据权限发送mq,sysKey={},topic={},value={}",sysKey,topic,value);
                ProducerRecord<String, String> arg0 = new ProducerRecord<String, String>(topic, value);
                Callback arg1 = new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata arg0, Exception arg1) {

                    }
                };
                KafkaProducerSingleton.getInstance(null).send(arg0, arg1);
            }
        }
    }
}
