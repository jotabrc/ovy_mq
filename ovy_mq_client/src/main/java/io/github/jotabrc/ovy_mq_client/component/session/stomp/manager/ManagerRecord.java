package io.github.jotabrc.ovy_mq_client.component.session.stomp.manager;

import io.github.jotabrc.ovy_mq_client.component.session.interfaces.SessionManager;
import io.github.jotabrc.ovy_mq_core.domain.Client;


public record ManagerRecord(Client client, SessionManager sessionManager) {

}
