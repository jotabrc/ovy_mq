package io.github.jotabrc.ovy_mq.security;

import io.github.jotabrc.ovy_mq.domain.ClientMapper;
import io.github.jotabrc.ovy_mq.domain.DefaultClientKey;
import io.github.jotabrc.ovy_mq.service.ConsumerRegistryImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;

import static java.util.Objects.nonNull;

@Getter
@Component
@AllArgsConstructor
public class ClientListener {

    private final ConsumerRegistryImpl clientRegistryImpl;

    @EventListener
    public void clientConnectionEventHandler(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> attributes = accessor.getSessionAttributes();

        if (isAttributesAvailable(attributes)) {
            String clientId = getClientId(attributes);
            String topic = accessor.getDestination();
            clientRegistryImpl.updateClientList(ClientMapper.of(clientId, topic));
        }
    }

    @EventListener
    public void clientDisconnectionEventHandler(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> attributes = accessor.getSessionAttributes();

        if (isAttributesAvailable(attributes)) {
            String clientId = getClientId(attributes);
            clientRegistryImpl.remove(clientId);
        }
    }

    private boolean isAttributesAvailable(Map<String, Object> attributes) {
        return nonNull(attributes) && !attributes.isEmpty();
    }

    private String getClientId(Map<String, Object> attributes) {
        String clientId = attributes.get(DefaultClientKey.CLIENT_ID.getValue()).toString();
        return nonNull(clientId)
                ? clientId
                : DefaultClientKey.CLIENT_ID_NOT_FOUND.getValue();
    }
}
