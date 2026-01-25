package io.github.jotabrc.ovy_mq_client.facade;

import io.github.jotabrc.ovy_mq_client.session.interfaces.Manager;
import io.github.jotabrc.ovy_mq_client.session.interfaces.client.ClientAdapter;
import io.github.jotabrc.ovy_mq_client.session.manager_handler.StompHealthCheckManager;
import io.github.jotabrc.ovy_mq_client.session.manager_handler.StompListenerPollManager;
import io.github.jotabrc.ovy_mq_client.session.manager_handler.stomp_handler.StompClientSessionHandler;
import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;

@RequiredArgsConstructor
@Component
public class ObjectProviderFacade {

    private final ObjectProvider<DefinitionMap> definitionMapProvider;
    private final ObjectProvider<StompHealthCheckManager> healthCheckManagerObjectProvider;
    private final ObjectProvider<StompListenerPollManager> listenerPollManagerObjectProvider;

    public DefinitionMap getDefinitionMap() {
        return definitionMapProvider.getObject();
    }

    public Manager provideStompHealthCheckManager(Client client, ClientAdapter<StompSession, WebSocketHttpHeaders, StompClientSessionHandler> clientAdapter) {
        StompHealthCheckManager stompHealthCheckManager = healthCheckManagerObjectProvider.getObject();
        stompHealthCheckManager.setClient(client);
        stompHealthCheckManager.setClientAdapter(clientAdapter);
        return stompHealthCheckManager;
    }

    public Manager getListenerPollManager(Client client, ClientAdapter<StompSession, WebSocketHttpHeaders, StompClientSessionHandler> clientAdapter) {
        StompListenerPollManager stompListenerPollManager = listenerPollManagerObjectProvider.getObject();
        stompListenerPollManager.setClient(client);
        stompListenerPollManager.setClientAdapter(clientAdapter);
        return stompListenerPollManager;
    }
}
