package io.github.jotabrc.ovy_mq_client.service.components.factory;

import io.github.jotabrc.ovy_mq_client.service.components.factory.domain.StompHeadersDto;
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
public class StompHeadersFactory implements AbstractFactory<StompHeadersDto, StompHeaders> {

    private final DefaultSecurityProvider securityProvider;

    @Override
    public StompHeaders create(StompHeadersDto dto) {
        StompHeaders headers = new StompHeaders();
        headers.setDestination(dto.getDestination());
        securityProvider.create(dto.getClientType()).forEach(headers::addAll);
        dto.getHeaders().forEach(headers::add);
        create(dto.getDestination(), dto.getTopic(), dto.getClientType()).forEach(headers::add);
        return headers;
    }

    @Override
    public Class<StompHeadersDto> supports() {
        return StompHeadersDto.class;
    }

    private Map<String, String> create(String destination, String topic, String clientType) {
        return Map.of(Key.FACTORY_DESTINATION, destination,
                Key.HEADER_TOPIC, topic,
                Key.HEADER_CLIENT_TYPE, clientType);
    }
}
