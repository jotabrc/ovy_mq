package io.github.jotabrc.ovy_mq_client.service.domain.client;

import io.github.jotabrc.ovy_mq_client.service.domain.client.interfaces.ClientSessionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Component
public class ClientSessionHandlerImpl implements ClientSessionHandler {

    private Map<String, ClientSession> sessions = new HashMap<>();

    @Override
    public <T, R> void execute(T t, R r) {
        if (t instanceof String topic && r instanceof ClientSession clientSession) {
            putIfAbsent(topic, clientSession);
        } else if (t instanceof String topic) {
            requestMessage(topic, r);
        }
    }

    @Override
    public void putIfAbsent(String topic, ClientSession clientSession) {
        if (nonNull(topic) && nonNull(clientSession) && !topic.isBlank()) {
            log.info("Saving session for topic {}", topic);
            sessions.putIfAbsent(topic, clientSession);
        }
    }

    @Override
    public void requestMessage(String topic, Object object) {
        sessions.get(topic).getSession().send("/topic/" + topic, object);
    }
}
