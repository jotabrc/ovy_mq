package io.github.jotabrc.ovy_mq.security;

import io.github.jotabrc.ovy_mq_core.defaults.Key;
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
        log.info("Request to registry received");
        Object clientIdAttribute = request.getAttributes().get(Key.HEADER_CLIEND_ID);
        Object topicAttribute = request.getAttributes().get(Key.HEADER_TOPIC);

        if (nonNull(clientIdAttribute) && nonNull(topicAttribute)) {
            String clientId = (String) clientIdAttribute;
            String topic = (String) topicAttribute;
            if (!clientId.isBlank() && !topic.isBlank()) {
                attributes.put(Key.HEADER_CLIEND_ID, clientId);
                attributes.put(Key.HEADER_TOPIC, topic);
                log.info("Registry: client={} successful", clientId);
                log.info("Topic subscription: client={} topic={}", clientId, topic);
                return true;
            }
        }

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        log.info("Registration failed - missing fields: clientId={} topic={}", clientIdAttribute, topicAttribute);
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
        Object clientIdAttribute = request.getAttributes().get(Key.HEADER_CLIEND_ID);

    }
}
