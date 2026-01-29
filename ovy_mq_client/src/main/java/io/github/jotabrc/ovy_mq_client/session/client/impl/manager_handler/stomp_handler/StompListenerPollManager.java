package io.github.jotabrc.ovy_mq_client.session.client.impl.manager_handler.stomp_handler;

import io.github.jotabrc.ovy_mq_client.messaging.message.ClientMessageDispatcher;
import io.github.jotabrc.ovy_mq_client.session.client.impl.manager_handler.ManagerFactory;
import io.github.jotabrc.ovy_mq_client.session.client.interfaces.ClientInitializer;
import io.github.jotabrc.ovy_mq_client.session.client.interfaces.ClientState;
import io.github.jotabrc.ovy_mq_client.session.client.interfaces.Manager;
import io.github.jotabrc.ovy_mq_core.domain.action.OvyAction;
import io.github.jotabrc.ovy_mq_core.domain.action.OvyCommand;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import io.github.jotabrc.ovy_mq_core.util.ValueUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static io.github.jotabrc.ovy_mq_core.constants.Mapping.SEND_COMMAND_TO_SERVER;

@Slf4j
@RequiredArgsConstructor
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class StompListenerPollManager implements Manager<StompSession, WebSocketHttpHeaders, StompClientSessionHandler> {

    private final ClientMessageDispatcher clientMessageDispatcher;
    private final ScheduledExecutorService scheduledExecutor;

    @Value("${ovymq.task.listener-poll.initial-delay:5000}")
    private Long initialDelay;
    @Value("${ovymq.task.listener-poll.fixed-delay:5000}")
    private Long fixedDelay;

    @Override
    public ScheduledFuture<?> execute(Client client,
                                      ClientState<StompSession, WebSocketHttpHeaders, StompClientSessionHandler> clientState,
                                      ClientInitializer<StompSession, WebSocketHttpHeaders, StompClientSessionHandler> clientInitializer) {
        return scheduledExecutor.scheduleWithFixedDelay(() -> {
                    if (client.getState().getAvailable().get()) {
                        log.info("Requesting message: client={} topic={}", client.getId(), client.getTopic());
                        OvyAction ovyAction = buildAction(client);
                        clientMessageDispatcher.send(client, SEND_COMMAND_TO_SERVER, ovyAction);
                    }
                }, ValueUtil.get(client.getPollInitialDelay(), initialDelay, client.useGlobalValues()),
                ValueUtil.get(client.getPollFixedDelay(), fixedDelay, client.useGlobalValues()),
                TimeUnit.MILLISECONDS);
    }

    private OvyAction buildAction(Client client) {
        return OvyAction.builder()
                .commands(List.of(OvyCommand.REQUEST_MESSAGE_PAYLOAD))
                .payload(client.getBasicClient())
                .build();
    }

    @Override
    public ManagerFactory factory() {
        return ManagerFactory.STOMP_LISTENER_POLL;
    }
}