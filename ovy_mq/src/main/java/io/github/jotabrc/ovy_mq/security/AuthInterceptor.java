package io.github.jotabrc.ovy_mq.security;

import io.github.jotabrc.ovy_mq.registry.ClientConfigurerContextHolder;
import io.github.jotabrc.ovy_mq_core.constants.OvyMqConstants;
import io.github.jotabrc.ovy_mq_core.domain.client.ClientConfigurer;
import io.github.jotabrc.ovy_mq_core.domain.client.ClientType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import java.util.Objects;

@Slf4j
@AllArgsConstructor
@Component
public class AuthInterceptor implements HandshakeInterceptor {

    private final ClientConfigurerContextHolder clientConfigurerContextHolder;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {
        Object clientIdAtt = request.getAttributes().get(OvyMqConstants.CLIENT_ID);
        Object topicAtt = request.getAttributes().get(OvyMqConstants.SUBSCRIBED_TOPIC);
        Object clientTypeAtt = request.getAttributes().get(OvyMqConstants.CLIENT_TYPE);

        try {
            if (clientIdAtt instanceof String clientId
                    && topicAtt instanceof String topic
                    && clientTypeAtt instanceof String clientType) {
                if (!clientId.isBlank() && !topic.isBlank() && !clientType.isBlank()) {
                    ClientType type = ClientType.valueOf(clientType);
                    attributes.put(OvyMqConstants.CLIENT_ID, clientId);
                    attributes.put(OvyMqConstants.SUBSCRIBED_TOPIC, topic);
                    attributes.put(OvyMqConstants.CLIENT_TYPE, type);
                    log.info("Handshake received: client={} topic={} clientType={}", clientId, topic, clientType);
                    return true;
                }
            }
        } catch (IllegalArgumentException e) {
            log.info("ClientType not supported clientType={}", clientTypeAtt);
        }

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        log.info("Handshake failed: client={} topic={} clientType={}", clientIdAtt, topicAtt, clientTypeAtt);
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
        Object clientId = request.getAttributes().get(OvyMqConstants.CLIENT_ID);
        Object clientType = request.getAttributes().get(OvyMqConstants.CLIENT_TYPE);
        if (clientType instanceof String type && clientId instanceof String id) {
            if (Objects.equals(ClientType.CONFIGURER, ClientType.valueOf(type)) && !id.isBlank()) {
                clientConfigurerContextHolder.add(ClientConfigurer.builder()
                        .id(id)
                        .type(ClientType.valueOf(clientType.toString()))
                        .build());
            }
        }
    }
}
