package io.github.jotabrc.ovy_mq_client.service.domain.client.handler;

import io.github.jotabrc.ovy_mq_client.domain.Client;
import io.github.jotabrc.ovy_mq_client.domain.factory.HttpHeaderFactory;
import io.github.jotabrc.ovy_mq_client.domain.factory.ObjectMapperFactory;
import io.github.jotabrc.ovy_mq_client.handler.ServerSubscribeException;
import io.github.jotabrc.ovy_mq_client.service.domain.client.ClientSession;
import io.github.jotabrc.ovy_mq_client.service.domain.client.handler.interfaces.ClientSessionInitializerHandler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.Objects.nonNull;

@Getter
@Slf4j
@RequiredArgsConstructor
@Component
public class ClientSessionInitializerHandlerImpl implements ClientSessionInitializerHandler {

    private final ObjectProvider<ClientSession> clientSessionProvider;
    private final HttpHeaderFactory httpHeaderFactory;

    @Override
    public void initializeSession(Client client) {
        log.info("Initializing session for client {}", client.getId());
        WebSocketStompClient stompClient = ObjectMapperFactory.getWithConverter();
        AtomicLong counter = new AtomicLong(0L);
        while (true) {
            if (connect(client, stompClient, counter)) return;
        }
    }

    private boolean connect(Client client, WebSocketStompClient stompClient, AtomicLong counter) {
        WebSocketHttpHeaders headers = httpHeaderFactory.get(client.getTopic());
        ClientSession clientSession = clientSessionProvider.getObject();

        try {
            StompSession session = connectToServerAndInitializeSubscription(client.getTopic(), stompClient, headers, clientSession);
            client.setClientSession(clientSession);
            clientSession.setClientId(client.getId());
            log.info("Session initialized {} for topic {}", session.getSessionId(), client.getTopic());
            return true;
        } catch (Exception e) {
            log.info("Server is unavailable, retrying connection. Retry number {} for client {} on topic {}", counter.getAndIncrement(), client.getId(), client.getTopic());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                log.error("Thread interrupted {}: {}", Thread.interrupted(), ex.getMessage());
            }
        }
        return false;
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
