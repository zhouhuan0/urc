package com.yks.urc.serialize.bp.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.serialize.bp.api.ISerializeBp;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

@Component
public class SerializeBpImpl implements ISerializeBp {

    Logger log = Logger.getLogger(this.getClass());

    @Override
    public String obj2Json(Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return null;
    }


    @Override
    public String obj2JsonNonEmpty(Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            //过滤为null和“”的属性、如上面的student中对address设为“” 此处不能打印出来
            mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return null;
    }

    /**
     * 支持自定义日期反序列化
     *
     * @return
     * @Author panyun@youkeshu.com
     * @Date 2019/6/15 16:51
     */
    public <T> T json2ObjNew(String json, TypeReference<T> typeRef) {
        if (StringUtility.isNullOrEmpty(json)) {
            return null;
        }
        try {
            SimpleModule serializerModule = new SimpleModule("CustomDateDeSerializer");
            serializerModule.addDeserializer(Date.class, new CustomDateDeSerializer());
            // 忽视 JSON 中的未知属性
            ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.registerModule(serializerModule);
            return mapper.readValue(json, typeRef);
        } catch (Exception e) {
            log.error(json, e);
        }
        return null;
    }

    @Override
    public <T> T json2Obj(String json, Class<T> valueType) {
        try {

            // 忽视 JSON 中的未知属性
            ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper.readValue(json, valueType);
        } catch (Exception e) {
            log.error(json, e);
        }
        return null;
    }
}

class CustomDateDeSerializer extends JsonDeserializer<Date> {
    @Override
    public Date deserialize(com.fasterxml.jackson.core.JsonParser p, com.fasterxml.jackson.databind.DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String source = p.getText().trim();
        return StringUtility.convertToDateNew(source, null);
    }
}