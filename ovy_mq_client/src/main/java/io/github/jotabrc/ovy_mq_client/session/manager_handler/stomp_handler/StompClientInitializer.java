package io.github.jotabrc.ovy_mq_client.session.manager_handler.stomp_handler;

import io.github.jotabrc.ovy_mq_client.session.SessionTimeoutManagerResolver;
import io.github.jotabrc.ovy_mq_client.session.SessionType;
import io.github.jotabrc.ovy_mq_client.session.interfaces.client.ClientHelper;
import io.github.jotabrc.ovy_mq_client.session.interfaces.client.ClientInitializer;
import io.github.jotabrc.ovy_mq_client.session.interfaces.client.ClientSession;
import io.github.jotabrc.ovy_mq_client.session.interfaces.client.ClientState;
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
    private ClientHelper<StompSession> clientHelper;
    private ClientState<StompSession, WebSocketHttpHeaders, StompClientSessionHandler> clientState;
    private ClientSession<StompSession, WebSocketHttpHeaders, StompClientSessionHandler> clientSession;

    @Override
    public List<ScheduledFuture<?>> initializeManagers(List<ManagerFactory> factories) {
        if (nonNull(this.clientHelper.getClient())) {
            return new ArrayList<>(managerFactoryResolver.initialize(this, clientState, clientHelper.getClient(), factories));
        } else throw new IllegalStateException("Cannot initialize any manager's with null Client");
    }

    @Override
    public CompletableFuture<ClientHelper<StompSession>> initializeSession() {
        log.info("Initializing-clientHelper client={}", this.clientHelper.getClient().getId());
        sessionTimeoutManagerResolver.get(SessionType.STOMP)
                .ifPresent(sessionTimeoutManager -> {
                    this.clientHelper.setConnectionFuture(new CompletableFuture<>());
                    sessionTimeoutManager.execute(clientHelper.getClient(), clientState, clientHelper, clientSession);
                });
        return this.clientHelper.getConnectionFuture();
    }

    @Override
    public void setClientHelper(ClientHelper<StompSession> clientHelper) {
        if (isNull(this.clientHelper) && nonNull(clientHelper))
            this.clientHelper = clientHelper;
    }

    @Override
    public void setClientState(ClientState<StompSession, WebSocketHttpHeaders, StompClientSessionHandler> clientState) {
        if (isNull(this.clientState) && nonNull(clientState))
            this.clientState = clientState;
    }

    @Override
    public void setClientSession(ClientSession<StompSession, WebSocketHttpHeaders, StompClientSessionHandler> clientSession) {
        if (isNull(this.clientSession) && nonNull(clientSession))
            this.clientSession = clientSession;
    }
}
