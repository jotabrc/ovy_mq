package io.github.jotabrc.ovy_mq_client.session.manager_handler.stomp_handler;

import io.github.jotabrc.ovy_mq_client.session.SessionTimeoutManagerResolver;
import io.github.jotabrc.ovy_mq_client.session.SessionType;
import io.github.jotabrc.ovy_mq_client.session.interfaces.client.ClientAdapter;
import io.github.jotabrc.ovy_mq_client.session.interfaces.client.ClientHelper;
import io.github.jotabrc.ovy_mq_client.session.interfaces.client.ClientInitializer;
import io.github.jotabrc.ovy_mq_client.session.manager_handler.ManagerFactory;
import io.github.jotabrc.ovy_mq_client.session.manager_handler.ManagerFactoryResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class StompClientInitializer implements ClientInitializer<StompSession, WebSocketHttpHeaders, StompClientSessionHandler> {

    private final ManagerFactoryResolver managerFactoryResolver;
    private final SessionTimeoutManagerResolver sessionTimeoutManagerResolver;
    private ClientAdapter<StompSession, WebSocketHttpHeaders, StompClientSessionHandler> clientAdapter;

    @Override
    public List<ScheduledFuture<?>> initializeManagers(List<ManagerFactory> factories) {
        if (nonNull(this.clientAdapter.getClientHelper().getClient())) {
            return new ArrayList<>(managerFactoryResolver.initialize(this.clientAdapter, factories));
        } else throw new IllegalStateException("Cannot initialize any manager's with null Client");
    }

    @Override
    public CompletableFuture<ClientHelper<?>> initializeSession() {
        log.info("Initializing-clientHelper client={}", this.clientAdapter.getClientHelper().getClient().getId());
        sessionTimeoutManagerResolver.get(SessionType.STOMP)
                .ifPresent(sessionTimeoutManager -> {
                    this.clientAdapter.getClientHelper().setConnectionFuture(new CompletableFuture<>());
                    sessionTimeoutManager.execute(this.clientAdapter)
                            .whenComplete((sessionManager, throwable) -> {
                                if (nonNull(sessionManager) && this.clientAdapter.getClientState().isConnected() && isNull(throwable))
                                    log.info("Session initialized: client={} topic={}", this.clientAdapter.getClientHelper().getClient().getId(), this.clientAdapter.getClientHelper().getClient().getTopic());
                                else
                                    log.info("Session failed to initialize: client={} topic={}", this.clientAdapter.getClientHelper().getClient().getId(), this.clientAdapter.getClientHelper().getClient().getTopic());
                            });
                });
        return this.clientAdapter.getClientHelper().getConnectionFuture();
    }

    @Override
    public void setClientAdapter(ClientAdapter<StompSession, WebSocketHttpHeaders, StompClientSessionHandler> clientAdapter) {
        if (isNull(this.clientAdapter)) this.clientAdapter = clientAdapter;
    }
}
