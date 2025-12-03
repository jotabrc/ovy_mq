package io.github.jotabrc.ovy_mq.registry;

import io.github.jotabrc.ovy_mq_core.domain.client.ServerClientConfigurer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class ConfigClientContextHolder {

    private final ConfigClientRegistry registry;

    public void add(ServerClientConfigurer serverClientConfigurer) {
        registry.add(serverClientConfigurer);
    }

    public Optional<String> getId() {
        return registry.get().map(ServerClientConfigurer::getId);
    }
}
