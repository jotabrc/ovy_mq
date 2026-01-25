package io.github.jotabrc.ovy_mq_client.session.manager_handler.stomp_handler;

import io.github.jotabrc.ovy_mq_client.facade.ObjectProviderFacade;
import io.github.jotabrc.ovy_mq_client.session.interfaces.client.ClientAdapter;
import io.github.jotabrc.ovy_mq_client.session.interfaces.client.ClientMessageSender;
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

@Slf4j
@RequiredArgsConstructor
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class StompClientMessageSender implements ClientMessageSender<StompSession, WebSocketHttpHeaders, StompClientSessionHandler> {

    private final ObjectProviderFacade objectProviderFacade;
    private final AbstractFactoryResolver abstractFactoryResolver;
    private ClientAdapter<StompSession, WebSocketHttpHeaders, StompClientSessionHandler> clientAdapter;

    @Override
    public void send(String destination,
                     Object payload) {
        synchronized (this) {
            Client client = clientAdapter.getClientHelper().getClient();
            DefinitionMap definition = objectProviderFacade.getDefinitionMap()
                    .add(OvyMqConstants.DESTINATION, destination)
                    .add(OvyMqConstants.SUBSCRIBED_TOPIC, client.getTopic())
                    .add(OvyMqConstants.CLIENT_TYPE, client.getType().name())
                    .add(OvyMqConstants.CLIENT_ID, client.getId());
            abstractFactoryResolver.create(definition, StompHeaders.class)
                    .ifPresent(headers -> {
                        if (clientAdapter.getClientState().isConnected()) {
                            clientAdapter.getClientHelper().getSession().send(headers, payload);
                        } else {
                            log.error("Failed to send message: client={} client-type={} clientHelper-connected={}",
                                    client.getId(), client.getType(), clientAdapter.getClientState().isConnected());
                        }
                    });
        }
    }

    @Override
    public void setClientAdapter(ClientAdapter<StompSession, WebSocketHttpHeaders, StompClientSessionHandler> clientAdapter) {
        if (isNull(this.clientAdapter)) this.clientAdapter = clientAdapter;
    }
}
