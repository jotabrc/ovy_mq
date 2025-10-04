package io.github.jotabrc.ovy_mq_client.service.domain.client;

import io.github.jotabrc.ovy_mq_client.config.CredentialConfig;
import io.github.jotabrc.ovy_mq_client.service.domain.client.interfaces.ClientSessionInitializerHandler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

@Getter
@Slf4j
@RequiredArgsConstructor
@Component
public class ClientSessionInitializerHandlerImpl implements ClientSessionInitializerHandler {

    private final CredentialConfig credentialConfig;

    @Override
    public <T> void execute(T t) {
        initializeSession((String) t);
    }

    @Override
    public void initializeSession(String topic) {
        log.info("Executing session initialization...");
        WebSocketStompClient stompClient = createDefaultClient();
        AtomicLong counter = new AtomicLong(0L);
        while (true) {
            if (connect(topic, stompClient, counter)) return;
        }
    }

    private boolean connect(String topic, WebSocketStompClient stompClient, AtomicLong counter) {
        WebSocketHttpHeaders headers = createHeaders(topic);
        ClientSession clientSession = SessionFactory.create();

        try {
            StompSession session = connectToServerAndInitializeSubscription(topic, stompClient, headers, clientSession);
            saveSession(topic, clientSession);
            log.info("Session initialized {} for topic {}", session.getSessionId(), topic);
            return true;
        } catch (Exception e) {
            log.info("Server not available, retry subscription ({}) for topic {}...", counter.getAndIncrement(), topic);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                log.error("Thread interrupted {}: {}", Thread.interrupted(), ex.getMessage());
            }
        }
        return false;
    }

    private WebSocketStompClient createDefaultClient() {
        StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        return stompClient;
    }

    private WebSocketHttpHeaders createHeaders(String topic) {
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        String basic = "Basic " + Base64.getEncoder().encodeToString((credentialConfig.getBcrypt()).getBytes(StandardCharsets.UTF_8));
        headers.put("Authorization", List.of(basic));
        headers.put("Listening-Topic", List.of(topic));
        return headers;
    }

    private StompSession connectToServerAndInitializeSubscription(String topic, WebSocketStompClient stompClient, WebSocketHttpHeaders headers, ClientSession clientSession) throws InterruptedException, ExecutionException, TimeoutException {
        stompClient.connectAsync("ws://localhost:9090/registry", headers, clientSession);
        StompSession session = clientSession.getFuture().get(5, TimeUnit.SECONDS);
        session.subscribe("/topic/" + topic, clientSession);
        clientSession.setSession(session);
        return session;
    }

    private void saveSession(String topic, ClientSession session) {
        ClientExecutor.CLIENT_SESSION.getHandler().execute(topic, session);
    }
}
