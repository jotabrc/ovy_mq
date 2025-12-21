package io.github.jotabrc.ovy_mq_client.factory.header;

import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.constants.OvyMqConstants;
import io.github.jotabrc.ovy_mq_core.components.factories.interfaces.AbstractFactory;
import io.github.jotabrc.ovy_mq_core.security.DefaultSecurityProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketHttpHeaderFactory implements AbstractFactory<WebSocketHttpHeaders> {

    private final DefaultSecurityProvider securityProvider;

    @Override
    public WebSocketHttpHeaders create(DefinitionMap definition) {
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        definition.convert(String.class).forEach(headers::add);
        securityProvider.createSimple(definition.extract(OvyMqConstants.CLIENT_TYPE, String.class))
                .forEach(headers::add);
        return headers;
    }

    @Override
    public Class<WebSocketHttpHeaders> supports() {
        return WebSocketHttpHeaders.class;
    }

    public static void main(String[] args) {
        System.out.println("Basic " + Base64.getEncoder().encodeToString(("1234").getBytes(StandardCharsets.UTF_8)));
    }
}
