package io.github.jotabrc.ovy_mq.security;

import io.github.jotabrc.ovy_mq.domain.DefaultClientKey;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

import static java.util.Objects.nonNull;

@Slf4j
@AllArgsConstructor
@Component
public class AuthInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {
        log.info("Request to registry acceptance received");
        Object clientIdAttribute = request.getAttributes().get(DefaultClientKey.CLIENT_ID.getValue());
        Object topicAttribute = request.getAttributes().get(DefaultClientKey.CLIENT_LISTENING_TOPIC.getValue());

        if (nonNull(clientIdAttribute) && nonNull(topicAttribute)) {
            String clientId = (String) clientIdAttribute;
            String topic = (String) topicAttribute;
            if (!clientId.isBlank() && !topic.isBlank()) {
                attributes.put(DefaultClientKey.CLIENT_ID.getValue(), clientId);
                attributes.put(DefaultClientKey.CLIENT_LISTENING_TOPIC.getValue(), topic);
                log.info("Registry for client {} was successful", clientId);
                log.info("Client {} is listening to listeningTopic {}", clientId, topic);
                return true;
            }
        }

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        log.info("Cannot finalize registration, missing fields: client id {}, listeningTopic {}", clientIdAttribute, topicAttribute);
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
        Object clientIdAttribute = request.getAttributes().get(DefaultClientKey.CLIENT_ID.getValue());

    }
}
