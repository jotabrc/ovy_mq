package io.github.jotabrc.ovy_mq_core.factories;

import io.github.jotabrc.ovy_mq_core.defaults.Key;
import io.github.jotabrc.ovy_mq_core.domain.ListenerConfig;
import io.github.jotabrc.ovy_mq_core.domain.ListenerState;
import io.github.jotabrc.ovy_mq_core.domain.Replica;
import io.github.jotabrc.ovy_mq_core.factories.interfaces.AbstractFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@RequiredArgsConstructor
@Component
public class ListenerConfigFactory implements AbstractFactory<ListenerConfig> {

    @Override
    public ListenerConfig create(Map<String, Object> definitions) {
        return ListenerConfig.builder()
                .listenerState(ListenerState.builder()
                        .topic(Key.extract(definitions, Key.HEADER_TOPIC, String.class))
                        .timeout(Key.extract(definitions, Key.FACTORY_CLIENT_TIMEOUT, Long.class))
                        .replica(Replica.builder()
                                .quantity(Key.extract(definitions, Key.FACTORY_REPLICA_QUANTITY, Integer.class))
                                .max(Key.extract(definitions, Key.FACTORY_REPLICA_MAX, Integer.class))
                                .min(Key.extract(definitions, Key.FACTORY_REPLICA_MIN, Integer.class))
                                .step(Key.extract(definitions, Key.FACTORY_REPLICA_STEP, Integer.class))
                                .autoManage(Key.extract(definitions, Key.FACTORY_REPLICA_AUTO_MANAGE, Boolean.class))
                                .build())
                        .build())
                .build();
    }

    @Override
    public Class<ListenerConfig> supports() {
        return ListenerConfig.class;
    }
}
