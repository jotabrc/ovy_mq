package io.github.jotabrc.ovy_mq_client.component.resource;

import io.github.jotabrc.ovy_mq_client.component.session.registry.SessionRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@RequiredArgsConstructor
public class AbstractResourceUtil {

    protected final SessionRegistry sessionRegistry;

    @Value("${ovymq.task.shutdown.wait-delay:1000}")
    protected Long waitDelay;
    @Value("${ovymq.task.shutdown.max-wait:180000}")
    protected Long maxWait;

    protected long elapsedTime(long startTime) {
        return System.currentTimeMillis() - startTime;
    }
}
