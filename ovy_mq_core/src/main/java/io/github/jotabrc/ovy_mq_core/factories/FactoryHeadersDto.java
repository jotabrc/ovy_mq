package io.github.jotabrc.ovy_mq_core.factories;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class FactoryHeadersDto<T, R> extends FactoryDto<T, R> {

    private final String destination;
    private final String topic;
    private final String clientType;
    private final String clientId;
    private final Map<String, String> headers = new HashMap<>();

    public FactoryHeadersDto(Class<T> type,
                             Class<R> returns,
                             String destination,
                             String topic,
                             String clientType,
                             String clientId,
                             Map<String, String> headers) {
        super(type, returns);
        this.destination = destination;
        this.topic = topic;
        this.clientType = clientType;
        this.clientId = clientId;
        this.headers.putAll(headers);
    }
}
