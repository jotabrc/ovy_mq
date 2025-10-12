package io.github.jotabrc.ovy_mq.service.handler.executor;

import io.github.jotabrc.ovy_mq.domain.MessageRecord;
import io.github.jotabrc.ovy_mq.service.handler.strategy.MessageRegistryStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MessageHandlerExecutor {

    public MessageRecord execute(MessageRegistryStrategy strategy, MessageRecord messageRecord) {
        return strategy.getHandler().handle(messageRecord);
    }
}
