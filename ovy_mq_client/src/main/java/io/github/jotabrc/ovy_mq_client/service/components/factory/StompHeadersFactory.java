package io.github.jotabrc.ovy_mq_client.service.components.factory;

import io.github.jotabrc.ovy_mq_client.service.components.factory.domain.StompHeadersDto;
import io.github.jotabrc.ovy_mq_core.defaults.Key;
import io.github.jotabrc.ovy_mq_core.factories.AbstractFactory;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class StompHeadersFactory implements AbstractFactory<StompHeadersDto, StompHeaders> {

    @Override
    public StompHeaders create(StompHeadersDto dto) {
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.setDestination(dto.getDestination());
        dto.getHeaders().forEach(stompHeaders::add);
        create(dto.getDestination(), dto.getTopic(), dto.getClientType()).forEach(stompHeaders::add);
        return stompHeaders;
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
