package io.github.jotabrc.ovy_mq.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jotabrc.ovy_mq.security.AuthPayload;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicReference;

@Component
@AllArgsConstructor
public class MessageWebSockerHandler implements WebSocketHandler {

    private final WebSocketSessionManager sessionManager;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        AtomicReference<String> clientId = new AtomicReference<>();

        return session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .take(1)
                .doOnNext(payloadContent -> {
                    try {
                        AuthPayload authPayload = new ObjectMapper().readValue(payloadContent, AuthPayload.class);
                        String basic = authPayload.getBasic();
                        // basic será um base64 com Bcrypt dentro, senha do bcrypt deve dar match entre cliente e mensageria
                        // envia basic para conferência
                        // consumer ID = authPayload.getClientName() + : + UUID
//                        sessionManager.register(consumer, token, session);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .thenMany(session.receive()
                        .doOnTerminate(() -> sessionManager.unregister(clientId.get())))
                .then();
    }
}
