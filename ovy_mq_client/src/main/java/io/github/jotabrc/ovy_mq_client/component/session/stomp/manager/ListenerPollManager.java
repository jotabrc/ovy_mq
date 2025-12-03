package io.github.jotabrc.ovy_mq_client.component.session.stomp.manager;

import io.github.jotabrc.ovy_mq_client.component.message.ClientMessageDispatcher;
import io.github.jotabrc.ovy_mq_client.component.session.interfaces.SessionManager;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import io.github.jotabrc.ovy_mq_core.util.ValueUtil;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static io.github.jotabrc.ovy_mq_core.defaults.Mapping.REQUEST_MESSAGE;
import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ListenerPollManager implements AbstractManager {

    private final ClientMessageDispatcher clientMessageDispatcher;
    private final ScheduledExecutorService scheduledExecutor;

    @Setter
    private SessionManager session;
    @Setter
    private Client client;
    private ScheduledFuture<?> taskFuture;

    @Value("${ovymq.task.listener-poll.initial-delay:10000}")
    private Long initialDelay;
    @Value("${ovymq.task.listener-poll.fixed-delay:35000}")
    private Long fixedDelay;

    @Override
    public ScheduledFuture<?> execute() {
        taskFuture = scheduledExecutor.scheduleWithFixedDelay(() -> {
                    if (client.getIsAvailable()) {
                        log.info("Requesting message: client={} topic={}", this.client.getId(), this.client.getTopic());
                        clientMessageDispatcher.send(this.client, this.client.getTopic(), REQUEST_MESSAGE, this.client.getTopic());
                    }
                }, ValueUtil.get(this.client.getPollInitialDelay(), this.initialDelay, this.client.useGlobalValues()),
                ValueUtil.get(this.client.getPollFixedDelay(), this.fixedDelay, this.client.useGlobalValues()),
                TimeUnit.MILLISECONDS);
        return taskFuture;
    }

    @Override
    public void destroy() {
        if (nonNull(taskFuture) && !taskFuture.isDone() && !taskFuture.isCancelled()) {
            taskFuture.cancel(true);
        }
    }
}