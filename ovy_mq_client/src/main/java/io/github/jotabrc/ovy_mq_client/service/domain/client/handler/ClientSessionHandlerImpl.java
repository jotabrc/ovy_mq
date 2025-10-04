package io.github.jotabrc.ovy_mq_client.service.domain.client.handler;

import io.github.jotabrc.ovy_mq_client.domain.Action;
import io.github.jotabrc.ovy_mq_client.service.domain.client.ClientSession;
import io.github.jotabrc.ovy_mq_client.service.domain.client.handler.interfaces.ClientSessionHandler;
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
    public void execute(Action action) {
        switch (action.getCommand()) {
            case EXECUTE_CLIENT_SESSION_HANDLER_PUT_IF_ABSENT -> putIfAbsent(action.getClient().getTopic(), action.getClient().getClientSession());
            case EXECUTE_CLIENT_SESSION_HANDLER_REQUEST_MESSAGE -> requestMessage(action.getClient().getTopic(), action.getMessagePayload());
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
