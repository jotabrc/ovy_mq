package io.github.jotabrc.ovy_mq.registry;

import io.github.jotabrc.ovy_mq_core.domain.ConfigClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class ConfigClientContextHolder {

    private final ConfigClientRegistry registry;

    public void add(ConfigClient configClient) {
        registry.add(configClient);
    }

    public Optional<String> getId() {
        return registry.get().map(ConfigClient::getId);
    }
}
