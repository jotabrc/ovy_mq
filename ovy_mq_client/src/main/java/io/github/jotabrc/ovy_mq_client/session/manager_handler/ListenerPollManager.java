package io.github.jotabrc.ovy_mq_client.session.manager_handler;

import io.github.jotabrc.ovy_mq_client.messaging.message.ClientMessageDispatcher;
import io.github.jotabrc.ovy_mq_core.domain.action.OvyAction;
import io.github.jotabrc.ovy_mq_core.domain.action.OvyCommand;
import io.github.jotabrc.ovy_mq_core.util.ValueUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static io.github.jotabrc.ovy_mq_core.constants.Mapping.SEND_COMMAND_TO_SERVER;

@Slf4j
@RequiredArgsConstructor
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ListenerPollManager extends AbstractManager {

    private final ClientMessageDispatcher clientMessageDispatcher;
    private final ScheduledExecutorService scheduledExecutor;

    @Value("${ovymq.task.listener-poll.initial-delay:10000}")
    private Long initialDelay;
    @Value("${ovymq.task.listener-poll.fixed-delay:35000}")
    private Long fixedDelay;

    @Override
    public ScheduledFuture<?> execute() {
        scheduledFuture = scheduledExecutor.scheduleWithFixedDelay(() -> {
                    if (client.getIsAvailable()) {
                        log.info("Requesting message: client={} topic={}", this.client.getId(), this.client.getTopic());
                        OvyAction ovyAction = buildAction();
                        clientMessageDispatcher.send(this.client, this.client.getTopic(), SEND_COMMAND_TO_SERVER, ovyAction);
                    }
                }, ValueUtil.get(this.client.getPollInitialDelay(), this.initialDelay, this.client.useGlobalValues()),
                ValueUtil.get(this.client.getPollFixedDelay(), this.fixedDelay, this.client.useGlobalValues()),
                TimeUnit.MILLISECONDS);
        return scheduledFuture;
    }

    private OvyAction buildAction() {
        return OvyAction.builder()
                .commands(List.of(OvyCommand.REQUEST_MESSAGE_PAYLOAD))
                .payload(this.client.getBasicClient())
                .build();
    }
}