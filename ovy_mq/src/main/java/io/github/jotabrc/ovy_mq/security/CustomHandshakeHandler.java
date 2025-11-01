package io.github.jotabrc.ovy_mq.security;

import io.github.jotabrc.ovy_mq.domain.defaults.Key;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
        return () -> attributes.get(Key.HEADER_CLIEND_ID).toString();
    }
}
