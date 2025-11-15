package io.github.jotabrc.ovy_mq_client.service.components.factory.domain;

import io.github.jotabrc.ovy_mq_core.factories.FactoryHeadersDto;
import lombok.Getter;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.util.Collections;
import java.util.Map;

@Getter
public class StompHeadersDto extends FactoryHeadersDto<StompHeadersDto, StompHeaders> {

    public StompHeadersDto(String destination,
                           String topic,
                           String clientType,
                           String clientId,
                           Map<String, String> headers) {
        super(StompHeadersDto.class, StompHeaders.class, destination, topic, clientType, clientId, headers);
    }

    public StompHeadersDto(String destination,
                           String topic,
                           String clientType,
                           String clientId) {
        super(StompHeadersDto.class, StompHeaders.class, destination, topic, clientType, clientId, Collections.emptyMap());
    }
}
