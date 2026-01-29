package io.github.jotabrc.ovy_mq_client.config;

import io.github.jotabrc.ovy_mq_client.session.client.impl.SessionType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SessionTypeConfig {

    @Value("${ovymq.session-manager.session-type.stomp-enabled:true}")
    private Boolean stompEnabled;

    public SessionType getSessionType() {
        return SessionType.STOMP;
    }
}
