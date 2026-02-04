package io.github.jotabrc.ovy_mq_core.components.mapper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

import static java.util.Objects.isNull;

public class PayloadSerializer extends StdSerializer<Object> {

    public PayloadSerializer() {
        super(Object.class);
    }

    @Override
    public void serialize(Object o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (isNull(o)) {
            jsonGenerator.writeNull();
        } else {
            serializerProvider.defaultSerializeValue(o, jsonGenerator);
        }
    }
}
