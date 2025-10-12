package io.github.jotabrc.ovy_mq.security;

import io.github.jotabrc.ovy_mq.domain.DefaultClientKey;
import io.github.jotabrc.ovy_mq.domain.factory.ClientFactory;
import io.github.jotabrc.ovy_mq.service.handler.executor.ClientHandlerExecutor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;

import static io.github.jotabrc.ovy_mq.service.handler.strategy.ClientRegistryStrategy.REMOVE;
import static io.github.jotabrc.ovy_mq.service.handler.strategy.ClientRegistryStrategy.UPSERT;
import static java.util.Objects.nonNull;

@Slf4j
@Getter
@Component
@AllArgsConstructor
@Deprecated
public class ClientListener {

    private final ClientHandlerExecutor clientHandlerExecutor;

    public void clientConnectionEventHandler(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> attributes = accessor.getSessionAttributes();

        if (isAttributesAvailable(attributes)) {
            String clientId = getClientId(attributes);
            String topic = getTopic(attributes);
            clientHandlerExecutor.execute(UPSERT, ClientFactory.of(clientId, topic));
        }
    }

    public void clientDisconnectionEventHandler(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> attributes = accessor.getSessionAttributes();

        if (isAttributesAvailable(attributes)) {
            String clientId = getClientId(attributes);
            log.info("Disconnecting client {}", clientId);
            clientHandlerExecutor.execute(REMOVE, ClientFactory.of(clientId));
        }
    }

    private boolean isAttributesAvailable(Map<String, Object> attributes) {
        return nonNull(attributes) && !attributes.isEmpty();
    }

    private String getClientId(Map<String, Object> attributes) {
        Object clientId = attributes.get(DefaultClientKey.CLIENT_ID.getValue());
        return nonNull(clientId) && !clientId.toString().isBlank()
                ? (String) clientId
                : DefaultClientKey.CLIENT_ID_NOT_FOUND.getValue();
    }

    private String getTopic(Map<String, Object> attributes) {
        Object topic = attributes.get(DefaultClientKey.CLIENT_LISTENING_TOPIC.getValue());
        return nonNull(topic) && !topic.toString().isBlank()
                ? (String) topic
                : DefaultClientKey.CLIENT_LISTENING_TOPIC_NOT_FOUND.getValue();
    }
}
