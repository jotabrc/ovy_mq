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

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.Objects.nonNull;

@Slf4j
@AllArgsConstructor
@Component
public class AuthInterceptor implements HandshakeInterceptor {

    private final SecurityHandler securityHandler;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        log.info("Request to registry acceptance received {}", request.getRemoteAddress());
        List<String> authHeaders = request.getHeaders().get("Authorization");

        if (nonNull(authHeaders) && !authHeaders.isEmpty()) {
            String[] credentials = securityHandler.retrieveCredentials(authHeaders.getFirst());
            if (securityHandler.hasCredentials(credentials)) {
                boolean credentialIsValid = securityHandler.validate(credentials[0]);
                if (credentialIsValid) {
                    String clientId = createClientId(credentials[1]);
                    log.info("Request authenticated successfully, new client registered: {}", clientId);
                    attributes.put(DefaultClientKey.CLIENT_ID.getValue(), clientId);
                    return true;
                }
            }
        }
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        log.info("Authentication failed, client is unauthorized {}", request.getRemoteAddress());
        return false;
    }

    private String createClientId(String clientName) {
        return clientName + ":" + UUID.randomUUID();
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {

    }
}
