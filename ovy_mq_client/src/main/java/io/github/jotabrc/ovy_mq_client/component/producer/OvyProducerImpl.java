package io.github.jotabrc.ovy_mq_client.component.producer;

import io.github.jotabrc.ovy_mq_client.component.producer.interfaces.OvyProducer;
import io.github.jotabrc.ovy_mq_client.component.session.interfaces.SessionManager;
import io.github.jotabrc.ovy_mq_core.defaults.Mapping;
import lombok.RequiredArgsConstructor;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
public class OvyProducerImpl implements OvyProducer {

    private final SessionManager sessionManager;

//    @Async(ThreadPoolConfig.PRODUCER_EXECUTOR)
    @Override
    public void send(Object payload) {
        if (isNull(payload))
            throw new IllegalArgumentException("Payload required to send message");
        this.sessionManager.send(Mapping.SAVE_MESSAGE, payload);
    }
}
