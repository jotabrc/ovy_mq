package io.github.jotabrc.ovy_mq_client.service.components.factory;

import io.github.jotabrc.ovy_mq_client.security.DefaultSecurityProvider;
import io.github.jotabrc.ovy_mq_client.service.components.factory.domain.WebSocketHttpHeadersDto;
import io.github.jotabrc.ovy_mq_core.defaults.Key;
import io.github.jotabrc.ovy_mq_core.factories.AbstractFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketHttpHeaderFactory implements AbstractFactory<WebSocketHttpHeadersDto, WebSocketHttpHeaders> {

    private final DefaultSecurityProvider securityProvider;

    @Override
    public WebSocketHttpHeaders create(WebSocketHttpHeadersDto dto) {
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.putAll(securityProvider.create(dto.getClientType()));
        dto.getHeaders().forEach(headers::add);
        create(dto.getDestination(), dto.getTopic(), dto.getClientType()).forEach(headers::add);
        return headers;
    }

    @Override
    public Class<WebSocketHttpHeadersDto> supports() {
        return WebSocketHttpHeadersDto.class;
    }

    private Map<String, String> create(String destination, String topic, String clientType) {
        return Map.of(Key.FACTORY_DESTINATION, destination,
                Key.HEADER_TOPIC, topic,
                Key.HEADER_CLIENT_TYPE, clientType);
    }

    public static void main(String[] args) {
        System.out.println("Basic " + Base64.getEncoder().encodeToString(("1234").getBytes(StandardCharsets.UTF_8)));
    }
}
