package io.github.jotabrc.ovy_mq_client.component.session.stomp.manager;

import io.github.jotabrc.ovy_mq_client.component.message.ClientMessageDispatcher;
import io.github.jotabrc.ovy_mq_client.component.session.interfaces.SessionManager;
import io.github.jotabrc.ovy_mq_core.domain.Client;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static io.github.jotabrc.ovy_mq_core.defaults.Mapping.REQUEST_MESSAGE;
import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ListenerPollManager {

    private final ClientMessageDispatcher clientMessageDispatcher;
    private final ScheduledExecutorService scheduledExecutor;

    @Setter
    private SessionManager session;
    @Setter
    private Client client;

    @Value("${ovymq.task.consumer.initial-delay:10000}")
    private Long initialDelay;
    @Value("${ovymq.task.consumer.fixed-delay:35000}")
    private Long fixedDelay;

    public void execute() {
        scheduledExecutor.scheduleWithFixedDelay(() -> {
                    if (client.getIsAvailable()) {
                        log.info("Requesting message: client={} topic={}", client.getId(), client.getTopic());
                        clientMessageDispatcher.send(client, client.getTopic(), REQUEST_MESSAGE, client.getTopic());
                    }
                }, getDelay(client.getPollInitialDelay(), this.initialDelay),
                getDelay(client.getPollFixedDelay(), this.fixedDelay),
                TimeUnit.MILLISECONDS);
    }

    private Long getDelay(Long value, Long defaultValue) {
        return nonNull(value)
                ? value
                : defaultValue;
    }
}