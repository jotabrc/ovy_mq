package io.github.jotabrc.ovy_mq.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jotabrc.ovy_mq.domain.MessagePayload;
import io.github.jotabrc.ovy_mq.handler.JsonToMessageException;
import io.github.jotabrc.ovy_mq.handler.MessageToJsonException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static java.util.Objects.nonNull;

@Profile("redis")
@Slf4j
@RequiredArgsConstructor
@Service
public class RedisRepository implements MessageRepository {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void saveToQueue(MessagePayload message) {
        try {
            String json = new ObjectMapper().writeValueAsString(message);
            redisTemplate.opsForList().rightPush(message.getTopic(), json);
        } catch (JsonProcessingException e) {
            throw new MessageToJsonException("Error while converting message to json: %s".formatted(message.getId()));
        }
    }

    private void savingErrorPrintLog(MessagePayload message) {
        log.info("Error while converting message to json: {}", message.getId());
    }

    @Override
    public MessagePayload removeFromQueueAndReturn(String topic) {
        String json = redisTemplate.opsForList().leftPop(topic);
        try {
            return new ObjectMapper().convertValue(json, MessagePayload.class);
        } catch (IllegalArgumentException e) {
            if (nonNull(json)) redisTemplate.opsForList().rightPush(topic, json);
            throw new JsonToMessageException(topic);
        }
    }

    @Override
    public List<MessagePayload> removeFromQueueAndReturnList(String topic, int quantity) {
        List<String> jsonList = redisTemplate.opsForList().leftPop(topic, quantity);
        if (nonNull(jsonList) && !jsonList.isEmpty()) {
            try {
                return createMessageFromJson(jsonList);
            } catch (Exception e) {
                returnMessagesToQueueInCaseOfFailure(topic, jsonList);
                throw new JsonToMessageException(topic);
            }
        }
        return Collections.emptyList();
    }

    private List<MessagePayload> createMessageFromJson(List<String> jsonList) {
        return jsonList.stream().map(json -> new ObjectMapper().convertValue(json, MessagePayload.class))
                .toList();
    }

    private void returnMessagesToQueueInCaseOfFailure(String topic, List<String> jsonList) {
        if (!jsonList.isEmpty()) jsonList.forEach(json -> redisTemplate.opsForList().rightPush(topic, json));
    }

    @Override
    public void removeFromProcessingQueue(String topic, String messageId) {
        redisTemplate.opsForList().getFirst(topic);
    }
}
