package io.github.jotabrc.ovy_mq_client.service.components;

import io.github.jotabrc.ovy_mq_client.config.CredentialConfig;
import io.github.jotabrc.ovy_mq_client.service.components.interfaces.AbstractFactory;
import io.github.jotabrc.ovy_mq_core.defaults.Key;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketHttpHeaderFactory implements AbstractFactory<WebSocketHttpHeaders, String> {

    private final CredentialConfig credentialConfig;

    @Override
    public WebSocketHttpHeaders create(Map<String, String> definitions) {
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        String basic = "Basic " + Base64.getEncoder().encodeToString((credentialConfig.getBcrypt()).getBytes(StandardCharsets.UTF_8));
        headers.put(Key.HEADER_AUTHORIZATION, List.of(basic));
        definitions.forEach(headers::add);
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
