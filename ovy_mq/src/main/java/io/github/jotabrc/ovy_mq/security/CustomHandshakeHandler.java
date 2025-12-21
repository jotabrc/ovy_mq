package io.github.jotabrc.ovy_mq.security;

import io.github.jotabrc.ovy_mq_core.constants.OvyMqConstants;
import io.github.jotabrc.ovy_mq_core.exception.OvyException;
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
            Object clientId = attributes.get(OvyMqConstants.CLIENT_ID);
            if (nonNull(clientId)
                    && clientId instanceof String client
                    && !client.isBlank())
                return client;
            throw new OvyException.AuthorizationDenied("Principal not available");
        };
    }
}
