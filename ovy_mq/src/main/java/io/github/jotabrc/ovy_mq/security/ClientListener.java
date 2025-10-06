package io.github.jotabrc.ovy_mq.security;

import io.github.jotabrc.ovy_mq.config.TaskConfig;
import io.github.jotabrc.ovy_mq.domain.ClientMapper;
import io.github.jotabrc.ovy_mq.domain.DefaultClientKey;
import io.github.jotabrc.ovy_mq.service.handler.interfaces.ClientRegistryHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;

import static java.util.Objects.nonNull;

@Slf4j
@Getter
@Component
@AllArgsConstructor
public class ClientListener {

    private final TaskConfig taskConfig;
    private final ClientRegistryHandler clientRegistryHandler;

    @EventListener
    public void clientConnectionEventHandler(SessionConnectEvent event) {
        if (taskConfig.useRegistry()) {
            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
            Map<String, Object> attributes = accessor.getSessionAttributes();

            if (isAttributesAvailable(attributes)) {
                String clientId = getClientId(attributes);
                String topic = getTopic(attributes);
                clientRegistryHandler.updateClientList(ClientMapper.of(clientId, topic));
            }
        }
    }

    @EventListener
    public void clientDisconnectionEventHandler(SessionDisconnectEvent event) {
        if (taskConfig.useRegistry()) {
            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
            Map<String, Object> attributes = accessor.getSessionAttributes();

            if (isAttributesAvailable(attributes)) {
                String clientId = getClientId(attributes);
                log.info("Disconnecting client {}", clientId);
                clientRegistryHandler.remove(clientId);
            }
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
