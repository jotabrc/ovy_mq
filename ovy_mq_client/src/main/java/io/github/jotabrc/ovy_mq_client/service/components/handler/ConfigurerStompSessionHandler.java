package io.github.jotabrc.ovy_mq_client.service.components.handler;

import io.github.jotabrc.ovy_mq_client.service.components.AbstractFactoryResolver;
import io.github.jotabrc.ovy_mq_client.service.registry.SessionRegistry;
import io.github.jotabrc.ovy_mq_core.defaults.Key;
import io.github.jotabrc.ovy_mq_core.domain.ListenerConfig;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static io.github.jotabrc.ovy_mq_core.defaults.Mapping.*;
import static java.util.Objects.isNull;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ConfigurerStompSessionHandler extends AbstractStompSessionHandler {

    public ConfigurerStompSessionHandler(PayloadHandlerDispatcher payloadHandlerDispatcher, AbstractFactoryResolver abstractFactoryResolver, PayloadConfirmationHandlerDispatcher payloadConfirmationHandlerDispatcher, SessionRegistry sessionRegistry, WebSocketStompClient webSocketStompClient) {
        super(payloadHandlerDispatcher, abstractFactoryResolver, payloadConfirmationHandlerDispatcher, sessionRegistry, webSocketStompClient);
    }

    /*
            TODO:
            1. New Handler for session connection avoiding code duplication
                1.1. MUST accept custom headers
            2. ConfigurerStompSessionHandler is a custom session for listener config and MUST be created regardless of @OvyListener annotation
             */
    @Override
    public void initialize() {
        synchronized (this) {
            log.info("Initializing-session client={}", this.client.getId());
            abstractFactoryResolver.getFactory(WebSocketHttpHeaders.class, String.class)
                    .ifPresent(factory -> {
                        Runnable connect = () -> {
                            Map<String, String> headerMap = new HashMap<>(Map.of(Key.FACTORY_DESTINATION, "server",
                                    Key.HEADER_TOPIC, this.client.getTopic(),
                                    Key.HEADER_CLIENT_TYPE, Key.HEADER_CLIENT_TYPE_CONFIGURER));
                            WebSocketHttpHeaders headers = factory.create(headerMap);
                            super.connect("ws://localhost:9090/" + WS_REGISTRY, headers);
                            this.client.setLastHealthCheck(OffsetDateTime.now());
                        };

                        AtomicInteger counter = new AtomicInteger(0);
                        while (counter.getAndIncrement() < connectionBackoff) {
                            try {
                                this.future = new CompletableFuture<>();
                                connect.run();
                                this.future.get(connectionTimeout, TimeUnit.MILLISECONDS);
                            } catch (Exception e) {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException ex) {
                                    Thread.currentThread().interrupt();
                                }
                                log.info("Server is unavailable, retrying connection. Retry-number={} client={} topic={}", counter.get(), this.client.getId(), this.client.getTopic());
                            }

                            if (Objects.equals(counter.get(), connectionBackoff) && isNull(this.session))
                                log.warn("Client={} Failed to connect to server after {} tries", client.getId(), counter.get());
                        }
                    });

            if (this.isConnected())
                log.info("Session initialized: client={} topic={}", client.getId(), client.getTopic());
        }
    }

    @NotNull
    @Override
    public Type getPayloadType(@NotNull StompHeaders headers) {
        String contentType = headers.getFirst(Key.HEADER_PAYLOAD_TYPE);
        if (Key.PAYLOAD_TYPE_LISTENER_CONFIG.equalsIgnoreCase(contentType)) return ListenerConfig.class;

        return Void.class;
    }

    @Override
    public void handleFrame(@NotNull StompHeaders headers, Object object) {
        payloadHandlerDispatcher.execute(client, object, headers);
    }

    @Override
    public void afterConnected(@NotNull StompSession session, @NotNull StompHeaders connectedHeaders) {
        this.session = session;
        this.subscribe(WS_USER + WS_CONFIG);
//        sessionRegistry.addOrReplace(client.getId(), this); // TODO remove or keep?
        this.future.complete(this);
    }
}
