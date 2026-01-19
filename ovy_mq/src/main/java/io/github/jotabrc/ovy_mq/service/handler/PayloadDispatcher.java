package io.github.jotabrc.ovy_mq.service.handler;

import io.github.jotabrc.ovy_mq.service.handler.interfaces.PayloadHandler;
import io.github.jotabrc.ovy_mq_core.domain.action.OvyAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PayloadDispatcher {

    private final PayloadHandlerRegistry payloadHandlerRegistry;

    public void execute(OvyAction ovyAction) {
        ovyAction.getCommands()
                .forEach(wsCommand -> payloadHandlerRegistry.getHandler(wsCommand)
                        .ifPresentOrElse(handler -> execute(handler, ovyAction),
                                () -> log.warn("No handler available for payload-class-{}", wsCommand)));
    }

    private void execute(PayloadHandler handler, OvyAction ovyAction) {
        handler.handle(ovyAction);
    }
}
