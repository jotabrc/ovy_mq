package io.github.jotabrc.ovy_mq_client.service.domain.client;

import io.github.jotabrc.ovy_mq_client.handler.MessageProcessingFailureException;
import io.github.jotabrc.ovy_mq_client.service.domain.client.interfaces.ClientMethodHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Component
public class ClientMethodHandlerImpl implements ClientMethodHandler {

    private Map<String, Method> clientMethods = new HashMap<>();

    @Override
    public <T, R> void execute(T t, R r) {
        if (t instanceof String && r instanceof Method) {
            putIfAbsent((String) t, (Method) r);
        } else if (t instanceof String topic) {
            invoke(topic, r);
        }
    }

    @Override
    public void putIfAbsent(String topic, Method method) {
        if (nonNull(topic) && nonNull(method) && !topic.isBlank()) {
            log.info("Saving client listener for topic {}", topic);
            clientMethods.putIfAbsent(topic, method);
        }
    }

    @Override
    public void invoke(String topic, Object object) {
        try {
            clientMethods.get(topic).invoke(object);
        } catch (Exception e) {
            throw new MessageProcessingFailureException(topic, e.getMessage());
        }
    }
}
