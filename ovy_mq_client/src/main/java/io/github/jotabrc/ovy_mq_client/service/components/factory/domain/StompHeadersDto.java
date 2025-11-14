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
                           Map<String, String> headers) {
        super(StompHeadersDto.class, StompHeaders.class, destination, topic, clientType, headers);
    }

    public StompHeadersDto(String destination,
                           String topic,
                           String clientType) {
        super(StompHeadersDto.class, StompHeaders.class, destination, topic, clientType, Collections.emptyMap());
    }
}
