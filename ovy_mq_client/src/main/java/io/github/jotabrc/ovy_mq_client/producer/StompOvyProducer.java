package io.github.jotabrc.ovy_mq_client.producer;

import io.github.jotabrc.ovy_mq_client.producer.interfaces.OvyProducer;
import io.github.jotabrc.ovy_mq_client.session.interfaces.SessionManager;
import io.github.jotabrc.ovy_mq_client.session.interfaces.SessionMessageSender;
import io.github.jotabrc.ovy_mq_core.constants.Mapping;
import lombok.RequiredArgsConstructor;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
public class StompOvyProducer implements OvyProducer {

    private final SessionManager sessionManager;

    //    @Async(ThreadPoolConfig.PRODUCER_EXECUTOR) todo
    @Override
    public void send(Object payload) {
        if (isNull(payload))
            throw new IllegalArgumentException("Payload required to send message");
        ((SessionMessageSender) this.sessionManager).send(Mapping.SAVE_MESSAGE, payload);
    }
}
