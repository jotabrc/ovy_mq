package io.github.jotabrc.ovy_mq_client.component.payload;

import io.github.jotabrc.ovy_mq_client.component.payload.interfaces.PayloadHandler;
import io.github.jotabrc.ovy_mq_core.domain.Client;
import io.github.jotabrc.ovy_mq_core.domain.ListenerConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ListenerConfigHandler implements PayloadHandler<ListenerConfig> {

    @Async
    @Override
    public void handle(Client client, ListenerConfig payload, StompHeaders headers) {
        handle(client, payload);
    }

    private void handle(Client client, ListenerConfig listenerConfig) {
        log.info("Configuring listener with topic={}: config=[quantity={} max={} min={} step={} autoManage={} timeout={}ms]",
                listenerConfig.getListenerState().getTopic(),
                listenerConfig.getListenerState().getReplica().getQuantity(),
                listenerConfig.getListenerState().getReplica().getMax(),
                listenerConfig.getListenerState().getReplica().getMin(),
                listenerConfig.getListenerState().getReplica().getMin(),
                listenerConfig.getListenerState().getReplica().getAutoManage(),
                listenerConfig.getListenerState().getTimeout());
        // TODO
    }

    @Override
    public Class<ListenerConfig> supports() {
        return ListenerConfig.class;
    }
}
