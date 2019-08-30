package com.yks.urc.fw;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.Date;

public class CustomDateDeSerializer extends JsonDeserializer<Date> {
    @Override
    public Date deserialize(com.fasterxml.jackson.core.JsonParser p, com.fasterxml.jackson.databind.DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String source = p.getText().trim();
        return StringUtility.convertToDate(source, null);
    }
}