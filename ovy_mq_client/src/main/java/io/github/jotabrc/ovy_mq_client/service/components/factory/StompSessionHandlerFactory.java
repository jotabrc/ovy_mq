package io.github.jotabrc.ovy_mq_client.service.components.factory;

import io.github.jotabrc.ovy_mq_client.service.components.factory.domain.StompSessionHandlerDto;
import io.github.jotabrc.ovy_mq_client.service.components.handler.StompSessionHandler;
import io.github.jotabrc.ovy_mq_core.factories.interfaces.AbstractFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

import static io.github.jotabrc.ovy_mq_core.defaults.Mapping.*;

@RequiredArgsConstructor
@Component
public class StompSessionHandlerFactory implements AbstractFactory<StompSessionHandlerDto, StompSessionHandler> {

    private final ObjectProvider<StompSessionHandler> provider;

    @Override
    public StompSessionHandler create(StompSessionHandlerDto dto) {
        StompSessionHandler sessionManager = provider.getObject();
        sessionManager.setClient(dto.getClient());
        sessionManager.setSubscriptions(addSubscriptions(dto));
        return sessionManager;
    }

    @Override
    public Class<StompSessionHandlerDto> supports() {
        return StompSessionHandlerDto.class;
    }

    private List<String> addSubscriptions(StompSessionHandlerDto dto) {
        return Stream.concat(dto.getSubscriptions().stream(), create(dto.getClient().getTopic()).stream())
                .toList();
    }

    private List<String> create(String topic) {
        return List.of(WS_USER + WS_HEALTH,
                WS_USER + WS_QUEUE + "/%s".formatted(topic));
    }
}
