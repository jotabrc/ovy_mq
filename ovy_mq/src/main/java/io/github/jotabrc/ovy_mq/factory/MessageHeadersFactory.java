package io.github.jotabrc.ovy_mq.factory;

import io.github.jotabrc.ovy_mq.factory.domain.MessageHeadersDto;
import io.github.jotabrc.ovy_mq_core.defaults.Key;
import io.github.jotabrc.ovy_mq_core.domain.ClientType;
import io.github.jotabrc.ovy_mq_core.factories.interfaces.AbstractFactory;
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
public class MessageHeadersFactory implements AbstractFactory<MessageHeadersDto, MessageHeaders> {

    private final DefaultSecurityProvider securityProvider;

    @Override
    public MessageHeaders create(MessageHeadersDto dto) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headers.setLeaveMutable(true);
        headers.setContentType(MimeTypeUtils.APPLICATION_JSON);
        headers.addNativeHeader(Key.HEADER_PAYLOAD_TYPE, dto.getContentType());
        securityProvider.createSimple(ClientType.SERVER.name()).forEach(headers::addNativeHeader);
        dto.getHeaders().forEach(headers::addNativeHeader);
        headers.setDestination(dto.getDestination());
        return headers.getMessageHeaders();
    }

    @Override
    public Class<MessageHeadersDto> supports() {
        return MessageHeadersDto.class;
    }
}
