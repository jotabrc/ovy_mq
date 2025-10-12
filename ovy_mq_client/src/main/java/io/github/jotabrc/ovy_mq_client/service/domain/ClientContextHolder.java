package io.github.jotabrc.ovy_mq_client.service.domain;

import io.github.jotabrc.ovy_mq_client.domain.Client;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Deprecated
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ClientContextHolder {

    private final ThreadLocal<Client> client = new ThreadLocal<>();

    public void setClient(Client client) {
        if (isNull(this.client.get())) {
            this.client.set(client);
        }
    }

    public Client getClient() {
        return this.client.get();
    }
}
