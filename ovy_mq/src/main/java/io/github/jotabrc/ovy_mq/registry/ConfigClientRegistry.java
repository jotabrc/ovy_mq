package io.github.jotabrc.ovy_mq.registry;

import io.github.jotabrc.ovy_mq_core.domain.ClientType;
import io.github.jotabrc.ovy_mq_core.domain.ConfigClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Component
public class ConfigClientRegistry {

    private ConfigClient client;

    public void add(ConfigClient configClient) {
        if (nonNull(configClient) && nonNull(configClient.getId()) && nonNull(configClient.getType())) {
            if (Objects.equals(ClientType.CONFIGURER, configClient.getType())) {
                log.info("Registry: config-client={}", configClient.getId());
                client = configClient;
            }
        }
    }

    public Optional<ConfigClient> get() {
        return Optional.ofNullable(client);
    }
}
