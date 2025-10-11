package io.github.jotabrc.ovy_mq_client.domain.factory;

import io.github.jotabrc.ovy_mq_client.config.CredentialConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@RequiredArgsConstructor
@Component
public class HttpHeaderFactory {

    private final CredentialConfig credentialConfig;

    public WebSocketHttpHeaders get(String topic) {
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        String basic = "Basic " + Base64.getEncoder().encodeToString((credentialConfig.getBcrypt()).getBytes(StandardCharsets.UTF_8));
        headers.put("Authorization", List.of(basic));
        headers.put("Listening-Topic", List.of(topic));
        return headers;
    }
}
