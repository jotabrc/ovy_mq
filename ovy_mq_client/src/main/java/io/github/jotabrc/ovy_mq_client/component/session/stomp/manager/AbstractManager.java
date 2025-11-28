package io.github.jotabrc.ovy_mq_client.component.session.stomp.manager;

import java.util.concurrent.ScheduledFuture;

public interface AbstractManager {

    ScheduledFuture<?> execute();
    void destroy();
}
