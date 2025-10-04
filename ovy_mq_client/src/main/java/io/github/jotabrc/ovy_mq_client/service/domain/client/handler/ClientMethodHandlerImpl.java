package io.github.jotabrc.ovy_mq_client.service.domain.client.handler;

import io.github.jotabrc.ovy_mq_client.domain.Action;
import io.github.jotabrc.ovy_mq_client.handler.MessageProcessingFailureException;
import io.github.jotabrc.ovy_mq_client.service.domain.client.handler.interfaces.ClientMethodHandler;
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
    public void execute(Action action) {
        switch (action.getCommand()) {
            case EXECUTE_CLIENT_METHOD_HANDLER_PUT_IF_ABSENT -> putIfAbsent(action.getClient().getTopic(), action.getClient().getMethod());
            case EXECUTE_CLIENT_METHOD_HANDLER_INVOKE -> invoke(action.getClient().getTopic(), action.getClient().getMethod());
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
    public void invoke(String topic, Method method) {
        try {
            clientMethods.get(topic).invoke(method);
        } catch (Exception e) {
            throw new MessageProcessingFailureException(topic, e.getMessage());
        }
    }
}
