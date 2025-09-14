package io.github.jotabrc.ovy_mq.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jotabrc.ovy_mq.domain.MessagePayload;
import io.github.jotabrc.ovy_mq.handler.JsonToMessageException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import static java.util.Objects.nonNull;

@Slf4j
@AllArgsConstructor
@Service
public class MessageRepositoryImpl implements MessageRepository {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void saveToQueue(MessagePayload message) {
        try {
            String json = new ObjectMapper().writeValueAsString(message);
            redisTemplate.opsForList().rightPush(message.createTopicKey(), json);
        } catch (JsonProcessingException e) {
            log.info("Error while converting message to json: {}", message.getId());
            log.info("Using auxiliary method of conversion to save message in queue: {}", message.getId());
            redisTemplate.opsForList().rightPush(message.createTopicKey(), message.toJSON());
        }
    }

    @Override
    public MessagePayload removeFromQueueAndReturn(String topic) {
        String json = redisTemplate.opsForList().leftPop(topic);
        try {
            return new ObjectMapper().convertValue(json, MessagePayload.class);
        } catch (IllegalArgumentException e) {
            if (nonNull(json)) redisTemplate.opsForList().rightPush(topic, json);
            throw new JsonToMessageException("Error while converting json to message from topic %s".formatted(topic));
        }
    }
}
