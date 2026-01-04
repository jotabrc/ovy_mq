package io.github.jotabrc.ovy_mq.registry;

import io.github.jotabrc.ovy_mq_core.domain.client.ClientConfigurer;
import io.github.jotabrc.ovy_mq_core.domain.client.ClientType;
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
public class ClientConfigurerRegistry {

    private ClientConfigurer client;

    public void add(ClientConfigurer clientConfigurer) {
        if (shouldAdd(clientConfigurer)) {
            log.info("Registry: config-client={}", clientConfigurer.getId());
            this.client = clientConfigurer;
        }
    }

    private boolean shouldAdd(ClientConfigurer clientConfigurer) {
        return isNull(this.client)
                && nonNull(clientConfigurer)
                && nonNull(clientConfigurer.getId())
                && nonNull(clientConfigurer.getType())
                && Objects.equals(ClientType.CONFIGURER, clientConfigurer.getType());
    }

    public Optional<ClientConfigurer> get() {
        return Optional.ofNullable(this.client);
    }
}
