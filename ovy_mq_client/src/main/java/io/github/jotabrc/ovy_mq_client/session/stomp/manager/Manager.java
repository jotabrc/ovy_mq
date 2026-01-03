package io.github.jotabrc.ovy_mq_client.session.stomp.manager;

import java.util.concurrent.ScheduledFuture;

public interface Manager {

    ScheduledFuture<?> execute();
    void destroy();
}
