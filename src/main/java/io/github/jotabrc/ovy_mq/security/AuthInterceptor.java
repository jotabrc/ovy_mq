package io.github.jotabrc.ovy_mq.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.Objects.nonNull;

@Component
public class AuthInterceptor implements HandshakeInterceptor {

    private final SecurityHandler securityHandler;

    public AuthInterceptor(SecurityHandler securityHandler) {
        this.securityHandler = securityHandler;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        List<String> authHeaders = request.getHeaders().get("Authorization");

        if (nonNull(authHeaders) && !authHeaders.isEmpty()) {
            String[] credentials = securityHandler.retrieveCredentials(authHeaders.getFirst());
            if (securityHandler.hasCredentials(credentials)) {
                boolean credentialIsValid = securityHandler.validate(credentials[0]);
                if (credentialIsValid) {
                    attributes.put("clientId", credentials[1] + ":" + UUID.randomUUID());
                    return true;
                }
            }
        }
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {

    }
}
