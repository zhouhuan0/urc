package com.yks.urc.mq.bp.impl;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.stereotype.Component;

import com.yks.mq.utils.KafkaProducerSingleton;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.mq.bp.api.IMqBp;
import com.yks.urc.vo.DataRuleSysVO;
import com.yks.urc.vo.DataRuleVO;

@Component
public class MqBpImpl implements IMqBp {

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
			KafkaProducerSingleton.getInstance().send(arg0, arg1);
		}
	}
}
