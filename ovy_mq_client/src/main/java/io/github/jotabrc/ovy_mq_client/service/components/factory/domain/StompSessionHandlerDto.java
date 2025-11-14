package io.github.jotabrc.ovy_mq_client.service.components.factory.domain;

import io.github.jotabrc.ovy_mq_client.service.components.handler.StompSessionHandler;
import io.github.jotabrc.ovy_mq_core.domain.Client;
import io.github.jotabrc.ovy_mq_core.factories.FactoryDto;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class StompSessionHandlerDto extends FactoryDto<StompSessionHandlerDto, StompSessionHandler> {

    private final Client client;
    private final List<String> subscriptions = new ArrayList<>();

    public StompSessionHandlerDto(Client client) {
        super(StompSessionHandlerDto.class, StompSessionHandler.class);
        this.client = client;
    }

    public StompSessionHandlerDto(Client client,
                                  List<String> subscriptions) {
        super(StompSessionHandlerDto.class, StompSessionHandler.class);
        this.client = client;
        this.subscriptions.addAll(subscriptions);
    }
}
