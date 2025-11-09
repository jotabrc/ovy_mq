package io.github.jotabrc.ovy_mq.service.handler;

import io.github.jotabrc.ovy_mq.domain.factory.HeaderFactory;
import io.github.jotabrc.ovy_mq.service.handler.interfaces.PayloadHandler;
import io.github.jotabrc.ovy_mq_core.defaults.Key;
import io.github.jotabrc.ovy_mq_core.defaults.Mapping;
import io.github.jotabrc.ovy_mq_core.domain.ListenerConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class PayloadListenerConfigHandler implements PayloadHandler<ListenerConfig> {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void handle(ListenerConfig listenerConfig) {
        log.info("Sending listener config: topic={} config=[replicas={} maxReplicas={} minReplicas={} stepReplicas={} timeout={} autoManageReplicas={}]",
                listenerConfig.getListenerState().getTopic(),
                listenerConfig.getListenerState().getReplicas(),
                listenerConfig.getListenerState().getMaxReplicas(),
                listenerConfig.getListenerState().getMinReplicas(),
                listenerConfig.getListenerState().getStepReplicas(),
                listenerConfig.getListenerState().getTimeout(),
                listenerConfig.getListenerState().getAutoManageReplicas());
        sendConfig(listenerConfig);
    }

    private void sendConfig(ListenerConfig listenerConfig) {
        messagingTemplate.convertAndSend(
                Mapping.WS_CONFIG + "/" + listenerConfig.getListenerState().getTopic(),
                listenerConfig,
                HeaderFactory.of(Key.PAYLOAD_TYPE_LISTENER_CONFIG));
    }

    @Override
    public Class<ListenerConfig> supports() {
        return ListenerConfig.class;
    }

    @Override
    public PayloadDispatcherCommand command() {
        return PayloadDispatcherCommand.LISTENER_CONFIG;
    }
}
