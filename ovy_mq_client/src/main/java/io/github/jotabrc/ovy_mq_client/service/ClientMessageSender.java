package io.github.jotabrc.ovy_mq_client.service;

import io.github.jotabrc.ovy_mq_client.domain.Client;
import io.github.jotabrc.ovy_mq_client.service.handler.interfaces.ClientSessionInitializerHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Component
public class ClientMessageSender {

    private final ClientSessionInitializerHandler clientSessionInitializerHandler;

    public void send(Runnable runnable, Client client) {
        if (nonNull(runnable)) {
            synchronized (client.getClientSessionHandler()) {
                log.info("Sending message: client={} runnable={}", client.getId(), runnable);
                if (!client.isConnected()) clientSessionInitializerHandler.initialize(client);
                runnable.run();
            }
        }
    }
}
