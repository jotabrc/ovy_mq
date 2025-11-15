package io.github.jotabrc.ovy_mq.factory.domain;

import io.github.jotabrc.ovy_mq_core.factories.FactoryDto;
import lombok.Getter;
import org.springframework.messaging.MessageHeaders;

import java.util.HashMap;
import java.util.Map;

@Getter
public class MessageHeadersDto extends FactoryDto<MessageHeadersDto, MessageHeaders> {

    private final String destination;
    private final String contentType;
    private final Map<String, String> headers = new HashMap<>();

    public MessageHeadersDto(String destination, String contentType, Map<String, String> headers) {
        super(MessageHeadersDto.class, MessageHeaders.class);
        this.destination = destination;
        this.contentType = contentType;
        this.headers.putAll(headers);
    }

    public MessageHeadersDto(String destination, String contentType) {
        super(MessageHeadersDto.class, MessageHeaders.class);
        this.destination = destination;
        this.contentType = contentType;
    }
}
