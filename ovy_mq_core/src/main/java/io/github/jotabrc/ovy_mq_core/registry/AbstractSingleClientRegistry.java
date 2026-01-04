package io.github.jotabrc.ovy_mq_core.registry;

import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import io.github.jotabrc.ovy_mq_core.domain.client.ClientType;
import io.github.jotabrc.ovy_mq_core.registry.interfaces.SingleRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@RequiredArgsConstructor
@Slf4j
@Component
public abstract class AbstractSingleClientRegistry implements SingleRegistry<Client> {

    private Client client;

    @Override
    public void add(Client client) {
        if (shouldAdd(client)) {
            log.info("{}: producer-client={}", this.getClass().getSimpleName(), client.getId());
            this.client = client;
        }
    }

    @Override
    public Optional<Client> get() {
        return Optional.ofNullable(this.client);
    }

    @Override
    public String getId() {
        return nonNull(this.client) ? client.getId() : "";
    }

    private boolean shouldAdd(Client client) {
        return isNull(this.client)
                && nonNull(client)
                && nonNull(client.getId())
                && nonNull(client.getType())
                && Objects.equals(ClientType.PRODUCER, client.getType());
    }
}
