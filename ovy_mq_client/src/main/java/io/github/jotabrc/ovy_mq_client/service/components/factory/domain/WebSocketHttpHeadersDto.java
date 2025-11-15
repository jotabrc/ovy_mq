package io.github.jotabrc.ovy_mq_client.service.components.factory.domain;

import io.github.jotabrc.ovy_mq_core.factories.FactoryHeadersDto;
import lombok.Getter;
import org.springframework.web.socket.WebSocketHttpHeaders;

import java.util.Collections;
import java.util.Map;

@Getter
public class WebSocketHttpHeadersDto extends FactoryHeadersDto<WebSocketHttpHeadersDto, WebSocketHttpHeaders> {

    public WebSocketHttpHeadersDto(String destination,
                                   String topic,
                                   String clientType,
                                   String clientId,
                                   Map<String, String> headers) {
        super(WebSocketHttpHeadersDto.class, WebSocketHttpHeaders.class, destination, topic, clientType, clientId, headers);
    }

    public WebSocketHttpHeadersDto(String destination,
                                   String topic,
                                   String clientType,
                                   String clientId) {
        super(WebSocketHttpHeadersDto.class, WebSocketHttpHeaders.class, destination, topic, clientType, clientId, Collections.emptyMap());
    }
}
