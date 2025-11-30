package io.github.jotabrc.ovy_mq_client.component.session.stomp.manager;

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
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SessionManagerDestroyManager implements AbstractManager {

    private final ScheduledExecutorService scheduledExecutor;

    @Setter
    private SessionManager sessionManager;
    @Setter
    private Client client;
    private ScheduledFuture<?> taskFuture;

    @Value("${ovymq.task.session-destroy.initial-delay:1000}")
    private Long initialDelay;
    @Value("${ovymq.task.session-destroy.fixed-delay:35000}")
    private Long fixedDelay;

    @Override
    public ScheduledFuture<?> execute() {
        taskFuture = scheduledExecutor.scheduleWithFixedDelay(() -> {
                    if (client.canDisconnect()) {
                        sessionManager.destroy();
                        this.destroy();
                    }
                }, this.initialDelay,
                this.fixedDelay,
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