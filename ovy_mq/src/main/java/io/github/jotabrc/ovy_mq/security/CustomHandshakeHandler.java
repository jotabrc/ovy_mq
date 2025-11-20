package io.github.jotabrc.ovy_mq.security;

import io.github.jotabrc.ovy_mq_core.defaults.Key;
import io.github.jotabrc.ovy_mq_core.defaults.Value;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

import static java.util.Objects.nonNull;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
        return () -> {
            Object clientId = attributes.get(Key.HEADER_CLIENT_ID);
            return nonNull(clientId)
                    && clientId instanceof String client
                    && !client.isBlank()
                    ? client
                    : Value.PRINCIPAL_IS_MISSING;
        };
    }
}
