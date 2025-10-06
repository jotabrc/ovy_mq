package io.github.jotabrc.ovy_mq_client.service.domain.client.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.jotabrc.ovy_mq_client.config.CredentialConfig;
import io.github.jotabrc.ovy_mq_client.domain.Client;
import io.github.jotabrc.ovy_mq_client.handler.ServerSubscribeException;
import io.github.jotabrc.ovy_mq_client.service.domain.client.ClientSession;
import io.github.jotabrc.ovy_mq_client.service.domain.client.SessionFactory;
import io.github.jotabrc.ovy_mq_client.service.domain.client.handler.interfaces.ClientSessionInitializerHandler;
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
import java.util.concurrent.atomic.AtomicLong;

import static java.util.Objects.nonNull;

@Getter
@Slf4j
@RequiredArgsConstructor
@Component
public class ClientSessionInitializerHandlerImpl implements ClientSessionInitializerHandler {

    private final CredentialConfig credentialConfig;

    @Override
    public void initializeSession(Client client) {
        log.info("Executing session initialization...");
        WebSocketStompClient stompClient = createDefaultClient();
        AtomicLong counter = new AtomicLong(0L);
        while (true) {
            if (connect(client, stompClient, counter)) return;
        }
    }

    private boolean connect(Client client, WebSocketStompClient stompClient, AtomicLong counter) {
        WebSocketHttpHeaders headers = createHeaders(client.getTopic());
        ClientSession clientSession = SessionFactory.create();

        try {
            StompSession session = connectToServerAndInitializeSubscription(client.getTopic(), stompClient, headers, clientSession);
            client.setClientSession(clientSession);
            log.info("Session initialized {} for topic {}", session.getSessionId(), client.getTopic());
            return true;
        } catch (Exception e) {
            log.info("Server not available, retry subscription ({}) for topic {}...", counter.getAndIncrement(), client.getTopic());
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

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(mapper);

        stompClient.setMessageConverter(converter);
        return stompClient;
    }

    private WebSocketHttpHeaders createHeaders(String topic) {
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        String basic = "Basic " + Base64.getEncoder().encodeToString((credentialConfig.getBcrypt()).getBytes(StandardCharsets.UTF_8));
        headers.put("Authorization", List.of(basic));
        headers.put("Listening-Topic", List.of(topic));
        return headers;
    }

    private StompSession connectToServerAndInitializeSubscription(String topic, WebSocketStompClient stompClient, WebSocketHttpHeaders headers, ClientSession clientSession) throws ExecutionException, InterruptedException {
        stompClient.connectAsync("ws://localhost:9090/registry", headers, clientSession);
        return clientSession.getFuture().whenComplete((returnedSession, exception) -> {
            if (nonNull(returnedSession) && returnedSession.isConnected()) {
                returnedSession.subscribe("/queue/" + topic, clientSession);
                returnedSession.subscribe("/config/", clientSession);
                returnedSession.subscribe("/user/queue/" + topic, clientSession);
                clientSession.setSession(returnedSession);
            } else {
                throw new ServerSubscribeException("Server not ready...");
            }
        }).get();
    }
}
