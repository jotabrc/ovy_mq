package io.github.jotabrc.ovy_mq.service.handler;

import io.github.jotabrc.ovy_mq.service.handler.interfaces.PayloadHandler;
import io.github.jotabrc.ovy_mq_core.domain.action.OvyCommand;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class PayloadHandlerRegistry {

    private final Map<OvyCommand, PayloadHandler> handlers = new HashMap<>();

    public PayloadHandlerRegistry(List<PayloadHandler> availableHandlers) {
        for (PayloadHandler handler : availableHandlers) {
            this.handlers.putIfAbsent(handler.command(), handler);
        }
    }

    public Optional<PayloadHandler> getHandler(OvyCommand command) {
        return Optional.ofNullable(this.handlers.get(command));
    }
}
