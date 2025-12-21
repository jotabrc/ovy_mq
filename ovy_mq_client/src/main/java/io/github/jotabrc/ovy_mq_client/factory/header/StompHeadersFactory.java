package io.github.jotabrc.ovy_mq_client.factory.header;

import io.github.jotabrc.ovy_mq_core.components.factories.interfaces.AbstractFactory;
import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.constants.OvyMqConstants;
import io.github.jotabrc.ovy_mq_core.security.DefaultSecurityProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompHeadersFactory implements AbstractFactory<StompHeaders> {

    private final DefaultSecurityProvider securityProvider;

    @Override
    public StompHeaders create(DefinitionMap definition) {
        StompHeaders headers = new StompHeaders();
        definition.convert(String.class).forEach(headers::add);
        headers.setDestination(definition.extract(OvyMqConstants.DESTINATION, String.class));
        securityProvider.createSimple(definition.extract(OvyMqConstants.CLIENT_TYPE, String.class))
                .forEach(headers::add);
        return headers;
    }

    @Override
    public Class<StompHeaders> supports() {
        return StompHeaders.class;
    }
}
