package io.github.jotabrc.ovy_mq_client.service.components;

import io.github.jotabrc.ovy_mq_client.config.CredentialConfig;
import io.github.jotabrc.ovy_mq_client.service.components.interfaces.OvyHeaderFactory;
import io.github.jotabrc.ovy_mq_core.defaults.Key;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketHttpHeaderFactory implements OvyHeaderFactory<WebSocketHttpHeaders> {

    private final CredentialConfig credentialConfig;

    @Override
    public WebSocketHttpHeaders createDefault(String destination, String topic) {
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        String basic = "Basic " + Base64.getEncoder().encodeToString((credentialConfig.getBcrypt()).getBytes(StandardCharsets.UTF_8));
        headers.put(Key.HEADER_AUTHORIZATION, List.of(basic));
        headers.put(Key.HEADER_TOPIC, List.of(topic));
        headers.put("server", List.of(destination));
        return headers;
    }

    @Override
    public Class<WebSocketHttpHeaders> supports() {
        return WebSocketHttpHeaders.class;
    }
}
