package io.github.jotabrc.ovy_mq_client.service.components;

import io.github.jotabrc.ovy_mq_client.service.components.interfaces.AbstractFactory;
import io.github.jotabrc.ovy_mq_core.defaults.Key;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component
public class StompHeaderFactory implements AbstractFactory<StompHeaders, String> {

    @Override
    public StompHeaders create(Map<String, String> definitions) {
        StompHeaders headers = new StompHeaders();
        definitions.forEach((key, value) -> {
            if (Objects.equals(Key.FACTORY_DESTINATION, key)) headers.setDestination(definitions.get(Key.FACTORY_DESTINATION));
            headers.add(key, value);
        });
        return headers;
    }

    @Override
    public Class<StompHeaders> supports() {
        return StompHeaders.class;
    }
}
