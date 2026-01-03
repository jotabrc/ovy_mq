package io.github.jotabrc.ovy_mq.registry;

import io.github.jotabrc.ovy_mq_core.domain.client.ClientConfigurer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class ClientConfigurerContextHolder {

    private final ClientConfigurerRegistry registry;

    public void add(ClientConfigurer clientConfigurer) {
        registry.add(clientConfigurer);
    }

    public Optional<String> getId() {
        return registry.get().map(ClientConfigurer::getId);
    }
}