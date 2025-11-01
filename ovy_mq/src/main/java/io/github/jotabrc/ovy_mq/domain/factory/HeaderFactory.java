package io.github.jotabrc.ovy_mq.domain.factory;

import io.github.jotabrc.ovy_mq.domain.defaults.Key;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.util.MimeTypeUtils;

public class HeaderFactory {

    private HeaderFactory() {}

    public static MessageHeaders of(String contentType) {
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        accessor.setContentType(MimeTypeUtils.APPLICATION_JSON);
        accessor.setLeaveMutable(true);
        accessor.setNativeHeader(Key.HEADER_PAYLOAD_TYPE, contentType);
        return accessor.getMessageHeaders();
    }
}
