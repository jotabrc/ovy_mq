package io.github.jotabrc.ovy_mq_client.session.manager_handler.stomp_handler;

import io.github.jotabrc.ovy_mq_client.session.interfaces.client.ClientHelper;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;

import static java.util.Objects.nonNull;

@Slf4j
@Setter
@Getter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class StompClientHelper implements ClientHelper<StompSession> {

    private Client client;
    private List<String> subscriptions;
    private StompSession session;
    private CompletableFuture<ClientHelper<?>> connectionFuture;
    private List<ScheduledFuture<?>> scheduledFutures;

    @Override
    public String getClientId() {
        return nonNull(this.client) ? client.getId() : "";
    }
}
