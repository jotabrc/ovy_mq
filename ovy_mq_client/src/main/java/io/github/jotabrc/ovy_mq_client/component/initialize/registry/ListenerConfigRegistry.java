package io.github.jotabrc.ovy_mq_client.component.initialize.registry;

import io.github.jotabrc.ovy_mq_core.domain.ListenerConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Component
public class ListenerConfigRegistry {

    private final Map<String, ListenerConfig> configs = new ConcurrentHashMap<>();

    public void save(ListenerConfig listenerConfig) {
        configs.put(listenerConfig.getListenerState().getTopic(), listenerConfig);
    }

    public Optional<ListenerConfig> get(String topic) {
        return Optional.ofNullable(configs.get(topic));
    }
}
