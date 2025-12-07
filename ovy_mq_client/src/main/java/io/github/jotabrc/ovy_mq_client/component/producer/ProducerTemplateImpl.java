package io.github.jotabrc.ovy_mq_client.component.producer;

import io.github.jotabrc.ovy_mq_client.component.producer.interfaces.ProducerTemplate;
import io.github.jotabrc.ovy_mq_client.component.session.interfaces.SessionManager;
import io.github.jotabrc.ovy_mq_client.config.ThreadPoolConfig;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
public class ProducerTemplateImpl implements ProducerTemplate {

    private SessionManager sessionManager;

    public void setSessionManager(SessionManager sessionManager) {
        if (isNull(this.sessionManager)) this.sessionManager = sessionManager;
    }

    @Async(ThreadPoolConfig.PRODUCER_EXECUTOR)
    @Override
    public void send(String topic, Object payload) {
        if (isNull(payload) || isNull(topic) || topic.isBlank())
            throw new IllegalArgumentException("Topic and payload required to send message");
        this.sessionManager.send(topic, payload);
    }
}
