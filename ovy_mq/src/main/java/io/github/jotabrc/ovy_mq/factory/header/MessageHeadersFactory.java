package io.github.jotabrc.ovy_mq.factory.header;

import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.constants.OvyMqConstants;
import io.github.jotabrc.ovy_mq_core.components.factories.interfaces.AbstractFactory;
import io.github.jotabrc.ovy_mq_core.security.DefaultSecurityProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

@Slf4j
@RequiredArgsConstructor
@Component
public class MessageHeadersFactory implements AbstractFactory<MessageHeaders> {

    private final DefaultSecurityProvider securityProvider;

    @Override
    public MessageHeaders create(DefinitionMap definition) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headers.setLeaveMutable(true);
        headers.setContentType(MimeTypeUtils.APPLICATION_JSON);
        definition.convert(String.class).forEach(headers::addNativeHeader);
        securityProvider.createSimple(definition.extract(OvyMqConstants.CLIENT_TYPE, String.class))
                .forEach(headers::addNativeHeader);
        return headers.getMessageHeaders();
    }

    @Override
    public Class<MessageHeaders> supports() {
        return MessageHeaders.class;
    }
}
