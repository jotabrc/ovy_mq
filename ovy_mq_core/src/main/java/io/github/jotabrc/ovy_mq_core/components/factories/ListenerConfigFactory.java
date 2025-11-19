package io.github.jotabrc.ovy_mq_core.components.factories;

import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.defaults.Key;
import io.github.jotabrc.ovy_mq_core.domain.ListenerConfig;
import io.github.jotabrc.ovy_mq_core.domain.ListenerState;
import io.github.jotabrc.ovy_mq_core.domain.Replica;
import io.github.jotabrc.ovy_mq_core.components.factories.interfaces.AbstractFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ListenerConfigFactory implements AbstractFactory<ListenerConfig> {

    @Override
    public ListenerConfig create(DefinitionMap definition) {
        return ListenerConfig.builder()
                .listenerState(ListenerState.builder()
                        .topic(definition.extract(Key.HEADER_TOPIC, String.class))
                        .timeout(definition.extract(Key.FACTORY_CLIENT_TIMEOUT, Long.class))
                        .replica(Replica.builder()
                                .quantity(definition.extract(Key.FACTORY_REPLICA_QUANTITY, Integer.class))
                                .max(definition.extract(Key.FACTORY_REPLICA_MAX, Integer.class))
                                .min(definition.extract(Key.FACTORY_REPLICA_MIN, Integer.class))
                                .step(definition.extract(Key.FACTORY_REPLICA_STEP, Integer.class))
                                .autoManage(definition.extractOrGet(Key.FACTORY_REPLICA_AUTO_MANAGE, Boolean.FALSE))
                                .build())
                        .build())
                .build();
    }

    @Override
    public Class<ListenerConfig> supports() {
        return ListenerConfig.class;
    }
}
