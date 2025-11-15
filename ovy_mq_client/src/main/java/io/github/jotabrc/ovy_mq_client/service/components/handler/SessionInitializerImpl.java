package io.github.jotabrc.ovy_mq_client.service.components.handler;

import io.github.jotabrc.ovy_mq_core.factories.AbstractFactoryResolver;
import io.github.jotabrc.ovy_mq_client.service.components.factory.domain.StompSessionHandlerDto;
import io.github.jotabrc.ovy_mq_client.service.components.handler.interfaces.SessionInitializer;
import io.github.jotabrc.ovy_mq_client.service.registry.SessionRegistry;
import io.github.jotabrc.ovy_mq_core.domain.Client;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Getter
@Slf4j
@RequiredArgsConstructor
@Component
public class SessionInitializerImpl implements SessionInitializer {

    private final AbstractFactoryResolver abstractFactoryResolver;
    private final SessionRegistry sessionRegistry;

    @Override
    public void createSessionAndConnect(Client client) {
        log.info("Creating SessionManager and initializing: client={}", client.getId());
        StompSessionHandlerDto dto = new StompSessionHandlerDto(client);
        abstractFactoryResolver.create(dto, dto.getReturns())
                .ifPresent(sessionManager -> {
                    sessionRegistry.addOrReplace(client.getId(), sessionManager);
                    sessionManager.initialize();
                });
    }
}
