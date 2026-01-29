package io.github.jotabrc.ovy_mq_client.session.client.impl.manager_handler.stomp_handler;

import io.github.jotabrc.ovy_mq_client.facade.ObjectProviderFacade;
import io.github.jotabrc.ovy_mq_client.session.client.interfaces.ClientHelper;
import io.github.jotabrc.ovy_mq_client.session.client.interfaces.ClientMessageSender;
import io.github.jotabrc.ovy_mq_client.session.client.interfaces.ClientState;
import io.github.jotabrc.ovy_mq_core.components.factories.AbstractFactoryResolver;
import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.constants.OvyMqConstants;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class StompClientMessageSender implements ClientMessageSender<StompSession, WebSocketHttpHeaders, StompClientSessionHandler> {

    private final ObjectProviderFacade objectProviderFacade;
    private final AbstractFactoryResolver abstractFactoryResolver;
    private ClientHelper<StompSession> clientHelper;
    private ClientState<StompSession, WebSocketHttpHeaders, StompClientSessionHandler> clientState;

    @Override
    public void send(String destination,
                     Object payload) {
        synchronized (this) {
            Client client = clientHelper.getClient();
            DefinitionMap definition = objectProviderFacade.getDefinitionMap()
                    .add(OvyMqConstants.DESTINATION, destination)
                    .add(OvyMqConstants.SUBSCRIBED_TOPIC, client.getTopic())
                    .add(OvyMqConstants.CLIENT_TYPE, client.getType().name())
                    .add(OvyMqConstants.CLIENT_ID, client.getId());
            abstractFactoryResolver.create(definition, StompHeaders.class)
                    .ifPresent(headers -> {
                        if (clientState.isConnected()) {
                            clientHelper.getSession().send(headers, payload);
                        } else {
                            log.error("Failed to send message: client={} client-type={} clientHelper-connected={}",
                                    client.getId(), client.getType(), clientState.isConnected());
                        }
                    });
        }
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
}
