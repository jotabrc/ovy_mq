package io.github.jotabrc.ovy_mq_client.service.handler;

import io.github.jotabrc.ovy_mq_client.domain.Client;
import io.github.jotabrc.ovy_mq_client.domain.factory.WebSocketHttpHeaderFactory;
import io.github.jotabrc.ovy_mq_client.domain.factory.ObjectMapperFactory;
import io.github.jotabrc.ovy_mq_client.handler.ServerSubscribeException;
import io.github.jotabrc.ovy_mq_client.service.handler.interfaces.ClientSessionInitializerHandler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.time.OffsetDateTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

import static io.github.jotabrc.ovy_mq_client.config.Mapping.*;
import static java.util.Objects.nonNull;

@Getter
@Slf4j
@RequiredArgsConstructor
@Component
public class ClientStompSessionInitializerHandlerImpl implements ClientSessionInitializerHandler {

    private final ObjectProvider<ClientSessionHandler> clientSessionProvider;
    private final WebSocketHttpHeaderFactory webSocketHttpHeaderFactory;

    @Override
    public void initialize(Client client) {
        log.info("Initializing-session client={}", client.getId());
        WebSocketStompClient stompClient = ObjectMapperFactory.getWithConverter();
        AtomicLong counter = new AtomicLong(0L);
        while (true) {
            if (connect(client, stompClient, counter)) return;
        }
    }

    private boolean connect(Client client, WebSocketStompClient stompClient, AtomicLong counter) {
        WebSocketHttpHeaders headers = webSocketHttpHeaderFactory.get(client.getTopic());
        ClientSessionHandler clientSessionHandler = clientSessionProvider.getObject();

        try {
            StompSession session = connectToServerAndInitializeSubscription(client.getTopic(), stompClient, headers, clientSessionHandler);
            clientSessionHandler.setClientId(client.getId());
            client.setClientSessionHandler(clientSessionHandler);
            client.setLastHealthCheck(OffsetDateTime.now());
            log.info("Session-initialized={} topic={}", session.getSessionId(), client.getTopic());
            return true;
        } catch (Exception e) {
            log.info("Server is unavailable, retrying connection. Retry-number={} client={} topic={}", counter.getAndIncrement(), client.getId(), client.getTopic());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                log.error("Thread-interrupted={}: {}", Thread.interrupted(), ex.getMessage());
            }
        }
        return false;
    }

    private StompSession connectToServerAndInitializeSubscription(String topic, WebSocketStompClient stompClient, WebSocketHttpHeaders headers, ClientSessionHandler clientSessionHandler) throws ExecutionException, InterruptedException {
        stompClient.connectAsync("ws://localhost:9090/" + WS_REGISTRY, headers, clientSessionHandler);
        return clientSessionHandler.getFuture().whenComplete((returnedSession, exception) -> {
            if (nonNull(returnedSession) && returnedSession.isConnected()) {
                returnedSession.subscribe(WS_USER + WS_HEALTH, clientSessionHandler);
                returnedSession.subscribe(WS_USER + WS_QUEUE + "/" + topic, clientSessionHandler);
            } else {
                throw new ServerSubscribeException("Server not ready...");
            }
        }).get();
    }
}
