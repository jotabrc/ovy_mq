package io.github.jotabrc.ovy_mq_client.component.factory.header;

import io.github.jotabrc.ovy_mq_core.defaults.Key;
import io.github.jotabrc.ovy_mq_core.factories.interfaces.AbstractFactory;
import io.github.jotabrc.ovy_mq_core.security.DefaultSecurityProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompHeadersFactory implements AbstractFactory<StompHeaders> {

    private final DefaultSecurityProvider securityProvider;

    @Override
    public StompHeaders create(Map<String, Object> definitions) {
        StompHeaders headers = new StompHeaders();
        Key.convert(definitions, String.class).forEach(headers::add);
        headers.setDestination(Key.extract(definitions, Key.HEADER_DESTINATION, String.class));
        securityProvider.createSimple(Key.extract(definitions, Key.HEADER_CLIENT_TYPE, String.class))
                .forEach(headers::add);
        return headers;
    }

    @Override
    public Class<StompHeaders> supports() {
        return StompHeaders.class;
    }

    private Map<String, String> create(String destination, String topic, String clientType, String clientId) {
        return Map.of(Key.HEADER_DESTINATION, destination,
                Key.HEADER_TOPIC, topic,
                Key.HEADER_CLIENT_TYPE, clientType,
                Key.HEADER_CLIENT_ID, clientId);
    }
}
