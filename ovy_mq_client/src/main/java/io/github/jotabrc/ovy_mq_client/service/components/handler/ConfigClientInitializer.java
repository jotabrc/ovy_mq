package io.github.jotabrc.ovy_mq_client.service.components.handler;

import io.github.jotabrc.ovy_mq_client.service.components.handler.interfaces.SessionInitializer;
import io.github.jotabrc.ovy_mq_client.service.registry.ClientRegistry;
import io.github.jotabrc.ovy_mq_core.domain.Client;
import io.github.jotabrc.ovy_mq_core.domain.ClientType;
import io.github.jotabrc.ovy_mq_core.factories.ClientFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ConfigClientInitializer implements ApplicationRunner {

    private final ClientRegistry clientRegistry;
    private final SessionInitializer sessionInitializer;

    @Value("${ovymq.session.connection.config-client.timeout}")
    private Long timeout;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initialize();
    }

    private void initialize() {
        Client client = ClientFactory.configClientOf(ClientType.CONFIGURER, timeout);
        sessionInitializer.createSessionAndConnect(client);
        clientRegistry.save(client);
    }
}
