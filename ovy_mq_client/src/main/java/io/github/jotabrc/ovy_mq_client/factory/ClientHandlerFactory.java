package io.github.jotabrc.ovy_mq_client.factory;

import io.github.jotabrc.ovy_mq_client.session.interfaces.client.ClientAdapter;
import io.github.jotabrc.ovy_mq_client.session.manager_handler.stomp_handler.StompClientSessionHandler;
import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.constants.OvyMqConstants;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;

@RequiredArgsConstructor
@Component
public class ClientHandlerFactory {

    private final ObjectProvider<ClientAdapter<StompSession, WebSocketHttpHeaders, StompClientSessionHandler>> stompClientAdapterProvider;

    public ClientAdapter<StompSession, WebSocketHttpHeaders, StompClientSessionHandler> buildHandlerForStomp(Client client, DefinitionMap definition) {
        var clientAdapter = stompClientAdapterProvider.getObject();

        var clientHelper = clientAdapter.getClientHelper();
        var clientInitializer = clientAdapter.getClientInitializer();
        var clientState = clientAdapter.getClientState();
        var clientMessageSender = clientAdapter.getClientMessageSender();
        var clientSession = clientAdapter.getClientSession();

        clientInitializer.setClientHelper(clientHelper);
        clientInitializer.setClientState(clientState);
        clientInitializer.setClientSession(clientSession);

        clientState.setClientHelper(clientHelper);

        clientMessageSender.setClientHelper(clientHelper);
        clientMessageSender.setClientState(clientState);

        clientSession.setClientHelper(clientHelper);

        clientHelper.setClient(client);
        clientHelper.setSubscriptions(definition.extractToList(OvyMqConstants.SUBSCRIPTIONS, String.class));
        return clientAdapter;
    }
}
