package io.github.jotabrc.ovy_mq.registry;

import io.github.jotabrc.ovy_mq_core.domain.client.ClientType;
import io.github.jotabrc.ovy_mq_core.domain.client.ServerClientConfigurer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Component
public class ConfigClientRegistry {

    private ServerClientConfigurer client;

    public void add(ServerClientConfigurer serverClientConfigurer) {
        if (shouldAdd(serverClientConfigurer)) {
            log.info("Registry: config-client={}", serverClientConfigurer.getId());
            client = serverClientConfigurer;
        }
    }

    private boolean shouldAdd(ServerClientConfigurer serverClientConfigurer) {
        return isNull(client)
                && nonNull(serverClientConfigurer)
                && nonNull(serverClientConfigurer.getId())
                && nonNull(serverClientConfigurer.getType())
                && Objects.equals(ClientType.CONFIGURER, serverClientConfigurer.getType());
    }

    public Optional<ServerClientConfigurer> get() {
        return Optional.ofNullable(client);
    }
}
