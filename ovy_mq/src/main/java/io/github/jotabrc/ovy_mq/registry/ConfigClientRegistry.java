package io.github.jotabrc.ovy_mq.registry;

import io.github.jotabrc.ovy_mq_core.domain.client.ServerClientConfigurer;
import io.github.jotabrc.ovy_mq_core.domain.client.ClientType;
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

    private ServerClientConfigurer client;

    public void add(ServerClientConfigurer serverClientConfigurer) {
        if (nonNull(serverClientConfigurer) && nonNull(serverClientConfigurer.getId()) && nonNull(serverClientConfigurer.getType())) {
            if (Objects.equals(ClientType.CONFIGURER, serverClientConfigurer.getType())) {
                log.info("Registry: config-client={}", serverClientConfigurer.getId());
                client = serverClientConfigurer;
            }
        }
    }

    public Optional<ServerClientConfigurer> get() {
        return Optional.ofNullable(client);
    }
}
