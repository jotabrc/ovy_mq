package io.github.jotabrc.ovy_mq_client.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jotabrc.ovy_mq_client.handler.MessagePayloadNotFoundException;

import java.io.IOException;

public class MessageMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private MessageMapper() {}

    public static MessagePayload toMessagePayload(String json) {
        try {
            return objectMapper.readValue(json, MessagePayload.class);
        } catch (JsonProcessingException e) {
            throw new MessagePayloadNotFoundException("Unable to convert JSON to MessagePayload");
        }
    }

    public static Object toTargetObject(byte[] payload, Class<?> targetType) {
        try {
            return objectMapper.readValue(payload, targetType);
        } catch (IOException e) {
            throw new MessagePayloadNotFoundException("Unable to convert payload to target Object");
        }
    }
}
