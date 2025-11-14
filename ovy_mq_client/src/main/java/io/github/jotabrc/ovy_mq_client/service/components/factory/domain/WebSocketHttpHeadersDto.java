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
                                   Map<String, String> headers) {
        super(WebSocketHttpHeadersDto.class, WebSocketHttpHeaders.class, destination, topic, clientType, headers);
    }

    public WebSocketHttpHeadersDto(String destination,
                                   String topic,
                                   String clientType) {
        super(WebSocketHttpHeadersDto.class, WebSocketHttpHeaders.class, destination, topic, clientType, Collections.emptyMap());
    }
}
