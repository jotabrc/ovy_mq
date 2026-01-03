package io.github.jotabrc.ovy_mq_client.session.interfaces;

import java.util.concurrent.ScheduledFuture;

public interface Manager {

    ScheduledFuture<?> execute();
    void destroy();
}
