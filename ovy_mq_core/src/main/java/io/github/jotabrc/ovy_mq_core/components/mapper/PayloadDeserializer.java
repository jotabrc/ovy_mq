package io.github.jotabrc.ovy_mq_core.components.mapper;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class PayloadDeserializer extends StdDeserializer<Object> {

    public PayloadDeserializer() {
        super(Object.class);
    }

    @Override
    public Object deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        Object o = deserializationContext.getAttribute("payloadType");

        if (o instanceof String payloadType) {
            Class<?> classType = null;
            try {
                classType = Class.forName(payloadType);
                return deserializationContext.readTreeAsValue(node, classType);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return deserializationContext.readTreeAsValue(node, Object.class);
    }
}
